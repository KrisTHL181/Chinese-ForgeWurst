/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 * Modified by KrisTHL181 in 2025
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;

public final class BoatFlyHack extends Hack {
    private final CheckboxSetting changeForwardSpeed = new CheckboxSetting(
		"允许更改前进速度",
		"允许\u00a7e'前进速度'\u00a7r被更改, 但这将禁用平滑加速.",
		false);

	private final SliderSetting forwardSpeed = new SliderSetting(
		"前进速度", 1, 0.05, 5, 0.05, SliderSetting.ValueDisplay.DECIMAL);
	
	private final SliderSetting upwardSpeed = new SliderSetting("向上速度",
		0.3, 0, 5, 0.05, SliderSetting.ValueDisplay.DECIMAL);
    public BoatFlyHack() {
		super("船飞", "让你的船可以飞起来.");
		setCategory(Category.MOVEMENT);
		addSetting(changeForwardSpeed);
		addSetting(forwardSpeed);
		addSetting(upwardSpeed);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        EntityPlayerSP player = mc.player;
        
        if (player == null || player.getRidingEntity() == null) {
            return;
        }

        Entity vehicle = player.getRidingEntity();
        
        // 默认速度
        double motionX = vehicle.motionX;
        double motionY = 0;
        double motionZ = vehicle.motionZ;

        // 处理上下移动
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            motionY = upwardSpeed.getValue();
        } else if (mc.gameSettings.keyBindSprint.isKeyDown()) {
            motionY = vehicle.motionY;
        }

        // 处理前进
        if (mc.gameSettings.keyBindForward.isKeyDown() && changeForwardSpeed.isChecked()) {
            double speed = forwardSpeed.getValue();
            double yawRad = Math.toRadians(vehicle.rotationYaw);
            motionX = -Math.sin(yawRad) * speed;
            motionZ = Math.cos(yawRad) * speed;
        }

        // 应用速度
        vehicle.motionX = motionX;
        vehicle.motionY = motionY;
        vehicle.motionZ = motionZ;
        vehicle.velocityChanged = true;
    }
}