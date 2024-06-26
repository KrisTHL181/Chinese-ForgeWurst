/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.wurstclient.forge.Command;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.MathUtils;

public final class HelpCmd extends Command {
	public HelpCmd() {
		super("help", "显示帮助.", "语法: .help <命令>",
				"列出命令列表: .help [<页码>]");
	}

	@Override
	public void call(String[] args) throws CmdException {
		if (args.length > 1)
			throw new CmdSyntaxError();

		String arg;
		if (args.length < 1)
			arg = "1";
		else
			arg = args[0];

		if (MathUtils.isInteger(arg))
			list(Integer.parseInt(arg));
		else
			help(arg);
	}

	private void list(int page) throws CmdException {
		List<Command> cmds = Arrays.<Command>asList(
				wurst.getCmds().getValues().toArray(new Command[0]));
		cmds.sort(Comparator.comparing(Command::getName));
		int size = cmds.size();

		int pages = Math.max((int) Math.ceil(size / 8.0), 1);
		if (page > pages || page < 1)
			throw new CmdSyntaxError("错误的页码: " + page);

		ChatUtils
				.message("总共: " + size + (size == 1 ? " 命令" : " 命令"));
		ChatUtils.message("命令列表 (第 " + page + " 页 /" + pages + " )");

		for (int i = (page - 1) * 8; i < Math.min(page * 8, size); i++) {
			Command c = cmds.get(i);
			ChatUtils.message("." + c.getName() + " - " + c.getDescription());
		}
	}

	private void help(String cmdName) throws CmdException {
		if (cmdName.startsWith("."))
			cmdName = cmdName.substring(1);

		Command cmd = wurst.getCmds().get(cmdName);
		if (cmd == null)
			throw new CmdSyntaxError("未知命令: ." + cmdName);

		ChatUtils.message("." + cmd.getName() + " - " + cmd.getDescription());
		for (String line : cmd.getSyntax())
			ChatUtils.message(line);
	}
}
