/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.wurstclient.forge.Command;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.Setting;

public final class SetCheckboxCmd extends Command {
	public SetCheckboxCmd() {
		super("setcheckbox", "修改复选框设置.",
				"语法: .setcheckbox <外挂> <复选框> <值>");
	}

	@Override
	public void call(String[] args) throws CmdException {
		if (args.length != 3)
			throw new CmdSyntaxError();

		Hack hack = wurst.getHax().get(args[0]);
		if (hack == null)
			throw new CmdError("外挂 \"" + args[0] + "\" 未找到.");

		Setting setting = hack.getSettings().get(args[1].toLowerCase().replace("_", " "));
		if (setting == null)
			throw new CmdError("设置 \"" + args[0] + " " + args[1]
					+ "\" 未找到.");

		if (!(setting instanceof CheckboxSetting))
			throw new CmdError(hack.getName() + " " + setting.getName()
					+ " 不是一个复选框.");
		CheckboxSetting e = (CheckboxSetting) setting;

		if (!args[2].equalsIgnoreCase("true")
				&& !args[2].equalsIgnoreCase("false"))
			throw new CmdSyntaxError("不是布尔类型: " + args[2]);

		e.setChecked(Boolean.parseBoolean(args[2]));
	}
}
