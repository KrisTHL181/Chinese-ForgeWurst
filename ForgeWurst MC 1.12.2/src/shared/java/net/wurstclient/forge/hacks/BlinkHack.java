/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import java.util.ArrayDeque;

import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.EntityFakePlayer;

@Hack.DontSaveState
public final class BlinkHack extends Hack {
	private final SliderSetting limit = new SliderSetting("限制",
			"自动重新启动瞬移一次\n" + "在给定的数据包个数后\n"
					+ "会被暂停.\n\n" + "0 = 无限制",
			0, 0, 500, 1, v -> v == 0 ? "禁用" : (int) v + "");

	private final ArrayDeque<CPacketPlayer> packets = new ArrayDeque<>();
	private EntityFakePlayer fakePlayer;

	public BlinkHack() {
		super("瞬移", "挂起所有移动更新.");
		setCategory(Category.MOVEMENT);
		addSetting(limit);
	}

	@Override
	public String getRenderName() {
		if (limit.getValueI() == 0)
			return getName() + " [" + packets.size() + "]";
		else
			return getName() + " [" + packets.size() + "/" + limit.getValueI()
					+ "]";
	}

	@Override
	protected void onEnable() {
		fakePlayer = new EntityFakePlayer();

		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);

		fakePlayer.despawn();
		packets.forEach(p -> mc.getConnection().sendPacket(p));
		packets.clear();
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (limit.getValueI() == 0)
			return;

		if (packets.size() >= limit.getValueI()) {
			setEnabled(false);
			setEnabled(true);
		}
	}

	@SubscribeEvent
	public void onPacketOutput(WPacketOutputEvent event) {
		if (!(event.getPacket() instanceof CPacketPlayer))
			return;

		event.setCanceled(true);

		CPacketPlayer packet = (CPacketPlayer) event.getPacket();
		CPacketPlayer prevPacket = packets.peekLast();

		if (prevPacket != null && packet.isOnGround() == prevPacket.isOnGround()
				&& packet.getYaw(-1) == prevPacket.getYaw(-1)
				&& packet.getPitch(-1) == prevPacket.getPitch(-1)
				&& packet.getX(-1) == prevPacket.getX(-1)
				&& packet.getY(-1) == prevPacket.getY(-1)
				&& packet.getZ(-1) == prevPacket.getZ(-1))
			return;

		packets.addLast(packet);
	}
}
