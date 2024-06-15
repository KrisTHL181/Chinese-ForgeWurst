/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import net.wurstclient.forge.Command;
import net.wurstclient.forge.KeybindList.Keybind;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.MathUtils;

public final class BindsCmd extends Command {
	public BindsCmd() {
		super("binds", "管理热键.", "语法: .binds add <键> <外挂名>",
				".binds add(添加) <键> <命令>", ".binds remove(删除) <键>",
				".binds list(列表) [<页码>]", ".binds remove-all(全部删除)", ".binds reset(重置)",
				"多个外挂/命令需要用';'分割.");
	}

	@Override
	public void call(String[] args) throws CmdException {
		if (args.length < 1)
			throw new CmdSyntaxError();

		switch (args[0].toLowerCase()) {
			case "add":
				add(args);
				break;

			case "remove":
				remove(args);
				break;

			case "list":
				list(args);
				break;

			case "remove-all":
				wurst.getKeybinds().removeAll();
				ChatUtils.message("热键已全部移除.");
				break;

			case "reset":
				wurst.getKeybinds().loadDefaults();
				ChatUtils.message("热键已全部重置.");
				break;

			case "添加":
				add(args);
				break;

			case "删除":
				remove(args);
				break;

			case "列表":
				list(args);
				break;

			case "全部删除":
				wurst.getKeybinds().removeAll();
				ChatUtils.message("热键已全部移除.");
				break;

			case "重置":
				wurst.getKeybinds().loadDefaults();
				ChatUtils.message("热键已全部重置.");
				break;

			default:
				throw new CmdSyntaxError();
		}
	}

	private void add(String[] args) throws CmdException {
		if (args.length < 3)
			throw new CmdSyntaxError();

		String key = args[1].toUpperCase();
		if (Keyboard.getKeyIndex(key) == Keyboard.KEY_NONE)
			throw new CmdSyntaxError("未知键: " + key);

		String commands = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

		wurst.getKeybinds().add(key, commands);
		ChatUtils.message("热键设置: " + key + " -> " + commands);
	}

	private void remove(String[] args) throws CmdException {
		if (args.length != 2)
			throw new CmdSyntaxError();

		String key = args[1].toUpperCase();
		if (Keyboard.getKeyIndex(key) == Keyboard.KEY_NONE)
			throw new CmdSyntaxError("未知键: " + key);

		String oldCommands = wurst.getKeybinds().getCommands(key);
		if (oldCommands == null)
			throw new CmdError("没有可移除的项.");

		wurst.getKeybinds().remove(key);
		ChatUtils.message("热键删除: " + key + " -> " + oldCommands);
	}

	private void list(String[] args) throws CmdException {
		if (args.length > 2)
			throw new CmdSyntaxError();

		int page;
		if (args.length < 2)
			page = 1;
		else if (MathUtils.isInteger(args[1]))
			page = Integer.parseInt(args[1]);
		else
			throw new CmdSyntaxError("不是数字: " + args[1]);

		int keybinds = wurst.getKeybinds().size();
		int pages = Math.max((int) Math.ceil(keybinds / 8.0), 1);
		if (page > pages || page < 1)
			throw new CmdSyntaxError("错误的页码: " + page);

		ChatUtils.message(
				"总共: " + keybinds + (keybinds == 1 ? " 热键" : " 热键"));
		ChatUtils.message("热键列表 (第 " + page + "页 / " + pages + ")");

		for (int i = (page - 1) * 8; i < Math.min(page * 8, keybinds); i++) {
			Keybind k = wurst.getKeybinds().get(i);
			ChatUtils.message(k.getKey() + " -> " + k.getCommands());
		}
	}
}
