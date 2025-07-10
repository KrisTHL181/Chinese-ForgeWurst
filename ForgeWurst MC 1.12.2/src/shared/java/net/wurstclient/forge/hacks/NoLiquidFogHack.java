/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 * Modified by KrisTHL181 in 2025
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class NoLiquidFogHack extends Hack {
	public NoLiquidFogHack() {
		super("去液体迷雾", "让你可以在液体中看的更清晰并删除水中的迷雾.");
		setCategory(Category.RENDER);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (event.getState().getMaterial().isLiquid()) {
            event.setDensity(0.0F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onFogColors(EntityViewRenderEvent.FogColors event) {
        if (event.getState().getMaterial().isLiquid()) {
            event.setRed(1.0F);
            event.setGreen(1.0F);
            event.setBlue(1.0F);
        }
    }

    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
        event.setCanceled(true);
    }
}

