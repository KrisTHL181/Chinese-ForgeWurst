/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 * Modified by KrisTHL181 in 2025
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.PacketBuffer;
import net.wurstclient.forge.Command;
import net.wurstclient.forge.utils.ChatUtils;

public final class DupeCmd extends Command
{
    public DupeCmd()
    {
        super("dupe", "使用'书与笔'复制手上的物品.\n使用方法:\n" +
                            "1. 将'书与笔'放在主手中.\n" +
                            "2. 把需要复制的物品放在你的物品栏中.\n" +
                            "3. 离开并重新连接服务器.\n" +
                            "4. 将物品放入箱子.\n" +
                            "5. 运行这个命令.", ".dupe");
    }

    @Override
    public void call(String[] args) throws CmdException
    {
        if(args.length > 0)
            throw new CmdSyntaxError();

        if(mc.player.getHeldItemMainhand().getItem() != Items.WRITABLE_BOOK)
        {
            ChatUtils.error("你必须先把'书与笔'放在主手上.");
            return;
        }

        NBTTagList listTag = new NBTTagList();

        StringBuilder builder1 = new StringBuilder();
        for(int i = 0; i < 21845; i++)
            builder1.append((char)2077);

        listTag.appendTag(new NBTTagString(builder1.toString()));

        StringBuilder builder2 = new StringBuilder();
        for(int i = 0; i < 21; i++)
            builder2.append("ForgeWurst!!");
        builder2.append("Kris");
        // 原代码为添加32个"Wurst!!!", 总长度为256; 此处为21个"ForgeWurst!!"+"Kris", 总长度也为256. (它们等价)

        String string2 = builder2.toString();
        for(int i = 0; i < 39; i++)
            listTag.appendTag(new NBTTagString(string2));

        ItemStack bookStack = new ItemStack(Items.WRITABLE_BOOK, 1);
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setString("title", "如果你可以看到这个, 则它无法正常运行.");
        nbt.setTag("pages", listTag);

        bookStack.setTagCompound(nbt);

        ByteBuf buffer = Unpooled.buffer();
        PacketBuffer packetBuffer = null;

        try
        {
            packetBuffer = new PacketBuffer(buffer);
            packetBuffer.writeItemStack(bookStack);

            mc.player.connection.sendPacket(new CPacketCustomPayload("MC|BEdit", packetBuffer));
        }
        catch (Exception e)
        {
            ChatUtils.error("发送书本数据包时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            if (packetBuffer != null && packetBuffer.refCnt() > 0) {
                packetBuffer.release();
            } else if (buffer.refCnt() > 0) {
                ChatUtils.warning("[内部故障] 释放ByteBuf时发生错误: 引用计数为" + buffer.refCnt() + ", 可能存在内存泄漏.");
                buffer.release();
            }
        }
    }
}