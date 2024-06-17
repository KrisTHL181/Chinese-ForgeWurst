/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import java.util.Random;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WPlayer;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;

public final class TpauraHack extends Hack {
	private final SliderSetting range = new SliderSetting("范围", 3, 1, 20, 0.05, ValueDisplay.DECIMAL);
	private final CheckboxSetting noFixedArea = new CheckboxSetting("固定范围", true);

	private boolean resetPos = true;
	private double posX = 0;
	private double posY = 0;
	private double posZ = 0;


	public TpauraHack() {
		super("传送光环", "自动攻击在你旁边的实体.\n"+"建议与'杀戮光环'一起使用.");
		setCategory(Category.MOVEMENT);
		addSetting(range);
		addSetting(noFixedArea);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		resetPos = true;
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		resetPos = true;
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		EntityPlayerSP player = event.getPlayer();
		World world = WPlayer.getWorld(player);

		if (resetPos){
			posX = player.posX;
            posY = player.posY;
            posZ = player.posZ;
            resetPos = false;
		}

		double rangeSq = Math.pow(range.getValue(), 2);
		double newPosX, newPosY, newPosZ;
		if (noFixedArea.isChecked()){
			newPosX = posX - rangeSq + (double)(Math.random() * posX + rangeSq - posX - rangeSq+1);
			newPosY = posY - rangeSq + (double)(Math.random() * posY + rangeSq - posY - rangeSq+1);
			newPosZ = posZ - rangeSq + (double)(Math.random() * posZ + rangeSq - posZ - rangeSq+1);
		}
		else {
			newPosX = player.posX - rangeSq + (double)(Math.random() * player.posX + rangeSq - player.posX - rangeSq+1);
			newPosY = player.posY - rangeSq + (double)(Math.random() * player.posY + rangeSq - player.posY - rangeSq+1);
			newPosZ = player.posZ - rangeSq + (double)(Math.random() * player.posZ + rangeSq - player.posZ - rangeSq+1);
		}
		player.setPosition(newPosX, newPosY, newPosZ);
		// 错误: 找不到符号
		// 符号:   变量 newPosX
		// 位置: 类 TpauraHack
	}
}
