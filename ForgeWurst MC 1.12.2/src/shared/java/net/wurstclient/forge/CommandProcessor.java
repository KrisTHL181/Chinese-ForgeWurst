/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WChatOutputEvent;
import net.wurstclient.forge.Command.CmdException;
import net.wurstclient.forge.utils.ChatUtils;

public final class CommandProcessor
{
	private final CommandList cmds;
	
	public CommandProcessor(CommandList cmds)
	{
		this.cmds = cmds;
	}
	
	@SubscribeEvent
	public void onSentMessage(WChatOutputEvent event)
	{
		String message = event.getMessage().trim();
		if(!message.startsWith("."))
			return;
		
		event.setCanceled(true);
		Minecraft.getMinecraft().ingameGUI.getChatGUI()
			.addToSentMessages(message);
		
		runCommand(message.substring(1));
	}
	
	public void runCommand(String input)
	{
		String[] parts = input.split(" ");
		Command cmd = cmds.get(parts[0]);
		
		if(cmd == null)
		{
			ChatUtils.error("未知命令: ." + parts[0]);
			if(input.startsWith("/"))
				ChatUtils.message(
					"使用 \".say " + input + "\" 来将其发送为一条聊天命令.");
			else
				ChatUtils
					.message("输入 \".help\" 获取命令列表 或 \".say ."
						+ input + "\" 来将其发送为一条聊天命令.");
			return;
		}
		
		try
		{
			cmd.call(Arrays.copyOfRange(parts, 1, parts.length));
			
		}catch(CmdException e)
		{
			e.printToChat();
		}
	}
}
