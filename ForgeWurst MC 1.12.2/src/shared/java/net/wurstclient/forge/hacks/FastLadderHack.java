/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WEntity;
import net.wurstclient.forge.settings.SliderSetting;
public final class FastLadderHack extends Hack
{
	private final SliderSetting climbingSpeed = new SliderSetting(
		"爬梯速度", 0.25, 0.05, 1, 0.05, SliderSetting.ValueDisplay.DECIMAL);
	public FastLadderHack()
	{
		super("快速爬梯", "让你能更快的爬上梯子.");
		setCategory(Category.MOVEMENT);
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
	public void onUpdate(WUpdateEvent event)
	{
		EntityPlayerSP player = event.getPlayer();
		
		if(!player.isOnLadder() || !WEntity.isCollidedHorizontally(player))
			return;
		
		if(player.movementInput.moveForward == 0
			&& player.movementInput.moveStrafe == 0)
			return;
		
		if(player.motionY < 0.25)
			player.motionY = climbingSpeed.getValue();
	}
}
