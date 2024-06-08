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
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.Setting;

public final class SetEnumCmd extends Command {
	public SetEnumCmd() {
		super("setenum", "修改枚举设置.",
				"语法: .setenum <外挂> <枚举> <值>");
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

		if (!(setting instanceof EnumSetting))
			throw new CmdError(
					hack.getName() + " " + setting.getName() + " 不是枚举.");
		EnumSetting<?> e = (EnumSetting<?>) setting;

		e.setSelected(args[2].replace("_", " "));
	}
}
