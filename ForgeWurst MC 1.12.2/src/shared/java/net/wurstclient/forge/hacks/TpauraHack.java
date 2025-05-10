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
	private final SliderSetting range = new SliderSetting("范围", 3, 1, 20, 0.5, ValueDisplay.DECIMAL);
	private final CheckboxSetting noFixedArea = new CheckboxSetting("固定区域", true);
	private final CheckboxSetting movePosY = new CheckboxSetting("移动高度", false);

	private boolean resetPos = true;
	private double posX = 0;
	private double posY = 0;
	private double posZ = 0;


	public TpauraHack() {
		super("传送光环", "自动在范围内移动.\n"+"建议与'杀戮光环'一起使用.");
		setCategory(Category.MOVEMENT);
		addSetting(range);
		addSetting(noFixedArea);
		addSetting(movePosY);
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

	public double randomInRange(double min, double max){
		Random rand = new Random();
		return (double)(min + (rand.nextDouble() * (max - min)));
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		EntityPlayerSP player = event.getPlayer();

		if (resetPos){
			posX = player.posX;
            posY = player.posY;
            posZ = player.posZ;
            resetPos = false;
		}

		double newPosX, newPosY, newPosZ;

		if (noFixedArea.isChecked()){
			newPosX = randomInRange(posX - range.getValue(), posX + range.getValue());
			newPosY = randomInRange(posY - range.getValue(), posY + range.getValue());
			newPosZ = randomInRange(posZ - range.getValue(), posZ + range.getValue());
		}
		else {
			newPosX = randomInRange(player.posX - range.getValue(), player.posX + range.getValue());
			newPosY = randomInRange(player.posY - range.getValue(), player.posY + range.getValue());
			newPosZ = randomInRange(player.posZ - range.getValue(), player.posZ + range.getValue());
		}

		if (movePosY.isChecked()){
			player.setPosition(newPosX, newPosY, newPosZ);
			return;
		}

		player.setPosition(newPosX, player.posY, newPosZ);
	}
}
