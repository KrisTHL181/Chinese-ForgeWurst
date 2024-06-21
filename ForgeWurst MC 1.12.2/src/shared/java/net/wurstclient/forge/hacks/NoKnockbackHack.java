/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraft.client.Minecraft;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;



public final class NoKnockbackHack extends Hack
{
	public NoKnockbackHack()
	{
		super("防击退",
			"让你能够不被其他玩家击退.");
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
    public void onKnockback(LivingKnockBackEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) { event.setCanceled(true); }
    }
}