/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.wurstclient.forge.Command;
import net.wurstclient.forge.utils.ChatUtils;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.Minecraft;

public final class IpCmd extends Command {
	public IpCmd() {
		super("ip", "获取服务器IP.",
				"语法: .ip");
	}

	@Override
	public void call(String[] args) throws CmdException {
		if (args.length > 0)
			throw new CmdSyntaxError();
		ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();
		if (serverData == null){
			ChatUtils.warning("获取失败.");
			return;
		}
		String serverIp = serverData.serverIP;
		ChatUtils.message("服务器IP: " + serverIp);
	}
}
