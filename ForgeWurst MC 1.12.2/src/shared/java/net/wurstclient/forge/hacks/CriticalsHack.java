/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 * Modified by KrisTHL181 in 2025
 * 
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt  
 */
package net.wurstclient.forge.hacks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;

public final class CriticalsHack extends Hack
{
    private final EnumSetting<Mode> mode = new EnumSetting<>("模式",
    "\u00a7l数据包\u00a7r模式会向服务器发送数据包, 而你实际上完全不会移动.\n\n"
        + "\u00a7l小幅跳跃\u00a7r模式会进行一次微小的跳跃, 刚好足以触发暴击.\n\n"
        + "\u00a7l完整跳跃\u00a7r模式会使你进行正常的跳跃.",
    Mode.values(), Mode.PACKET);

    public CriticalsHack()
    {
        super("强制暴击", "让你的每次攻击都可以是暴击."); // 假设 Hack 基类构造函数不变
        setCategory(Category.COMBAT);
        addSetting(mode);
    }

    @Override
    protected void onEnable()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    protected void onDisable()
    {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event)
    {
        if (!event.getEntityPlayer().equals(mc.player)) {
            return;
        }

        Entity target = event.getTarget();

        if (!(target instanceof EntityLivingBase))
            return;

        if (!mc.player.onGround)
            return;

        if (mc.player.isInWater() || mc.player.isInLava())
            return;

        switch (mode.getSelected())
        {
            case PACKET:
                doPacketJump();
                break;

            case MINI_JUMP:
                doMiniJump();
                break;

            case FULL_JUMP:
                doFullJump();
                break;
        }
    }

    private void doPacketJump()
    {
        sendFakeY(0.0625D, true);
        sendFakeY(0.0D, false);
        sendFakeY(1.1e-5D, false);
        sendFakeY(0.0D, false);
    }

    private void sendFakeY(double offset, boolean onGround)
    {
        mc.player.connection.sendPacket(
            new CPacketPlayer.Position(mc.player.posX, mc.player.posY + offset,
                mc.player.posZ, onGround));
    }

    private void doMiniJump()
    {
        mc.player.motionY = 0.1;
        mc.player.fallDistance = 0.1F;
        mc.player.onGround = false;
    }

    private void doFullJump()
    {
        mc.player.jump();
    }

    private enum Mode
    {
        PACKET("仅数据包"),
        MINI_JUMP("小幅跳跃"),
        FULL_JUMP("完整跳跃");

        private final String name;

        private Mode(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }
}