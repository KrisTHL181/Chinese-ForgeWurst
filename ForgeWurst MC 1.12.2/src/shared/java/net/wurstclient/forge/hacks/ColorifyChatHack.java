/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 * Modified by KrisTHL181 in 2025
 * 
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class ColorifyChatHack extends Hack {
	public ColorifyChatHack() {
		super("颜色转义", "让你在聊天中发送的 & 可以被转义成颜色字符.");
		setCategory(Category.MOVEMENT);
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
    public void onClientChat(ClientChatEvent event) {
        String original = event.getMessage();
        String modified = original.replace("&", "§");
        event.setMessage(modified);
    }
}

