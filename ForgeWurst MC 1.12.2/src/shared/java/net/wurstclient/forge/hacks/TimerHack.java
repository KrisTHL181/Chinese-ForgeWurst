/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import java.lang.reflect.Field;

import net.minecraft.util.Timer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;

public final class TimerHack extends Hack {
	private final SliderSetting speed = new SliderSetting("速度", 2, 0.1, 20, 0.1, ValueDisplay.DECIMAL);

	public TimerHack() {
		super("变速器", "改变一切的速度.");
		setCategory(Category.OTHER);
		addSetting(speed);
	}

	@Override
	public String getRenderName() {
		return getName() + " [" + speed.getValueString() + "]";
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		setTickLength(50);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		setTickLength(50 / speed.getValueF());
	}

	private void setTickLength(float tickLength) {
		try {
			Field fTimer = mc.getClass().getDeclaredField(
					wurst.isObfuscated() ? "field_71428_T" : "timer");
			fTimer.setAccessible(true);

			Field fTickLength = Timer.class.getDeclaredField(
					wurst.isObfuscated() ? "field_194149_e" : "tickLength");
			fTickLength.setAccessible(true);
			fTickLength.setFloat(fTimer.get(mc), tickLength);

		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}
