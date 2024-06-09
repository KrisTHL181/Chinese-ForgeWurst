/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.utils.ChatUtils;

public class WurstUpdater {
	private Thread thread;
	private ITextComponent component;

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (thread == null) {
			thread = new Thread(() -> checkForUpdates());
			thread.start();
			return;
		}

		if (thread.isAlive())
			return;

		if (component != null)
			ChatUtils.component(component);

		MinecraftForge.EVENT_BUS.unregister(this);
	}

	private void checkForUpdates() {
		Version newVersion = null;

		try {
			JsonElement json = fetchJson(
					"https://raw.githubusercontent.com/KrisTHL181/Chinese-ForgeWurst/master/update.json");
			JsonElement promos = json.getAsJsonObject().get("promos");
			JsonElement recommended = promos.getAsJsonObject()
					.get(WMinecraft.VERSION + "-recommended");
			newVersion = new Version(recommended.getAsString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (newVersion == null || newVersion.isInvalid()) {
			component = new TextComponentString(
					"在检查更新时发生了异常."
							+ " 点击 \u00a7n这里\u00a7r 来手动检测.");
			ClickEvent event = new ClickEvent(ClickEvent.Action.OPEN_URL,
					"https://forge.wurstclient.net/download/");
			component.getStyle().setClickEvent(event);
			return;
		}

		if (!newVersion.isHigherThan(ForgeWurst.VERSION))
			return;

		component = new TextComponentString(
				"ForgeWurst " + newVersion + " 可用."
						+ " 点击 \u00a7n这里\u00a7r 来下载更新.");
		ClickEvent event = new ClickEvent(ClickEvent.Action.OPEN_URL,
				"https://forge.wurstclient.net/download/");
		component.getStyle().setClickEvent(event);
	}

	private JsonElement fetchJson(String url) throws IOException {
		try (InputStream in = URI.create(url).toURL().openStream()) {
			return new JsonParser()
					.parse(new BufferedReader(new InputStreamReader(in)));
		}
	}
}
