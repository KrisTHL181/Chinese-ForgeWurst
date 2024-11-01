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
import net.wurstclient.forge.settings.Setting;
import net.wurstclient.forge.settings.LongTypeSliderSetting;
import net.wurstclient.forge.utils.MathUtils;

public final class SetLongTypeSliderCmd extends Command {
	public SetLongTypeSliderCmd() {
		super("setlongtypeslider", "修改长类型滑块设置.",
				"语法: .setlongtypeslider <外挂名> <滑块> <值>");
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

		if (!(setting instanceof LongTypeSliderSetting))
			throw new CmdError(
					hack.getName() + " " + setting.getName() + " 不是长类型滑块.");
		LongTypeSliderSetting slider = (LongTypeSliderSetting) setting;

		if (MathUtils.isLong(args[2]))
			slider.setValue(Long.parseLong(args[2]));
		else if (args[2].startsWith("~")
				&& MathUtils.isLong(args[2].substring(1)))
			slider.setValue(
					slider.getValue() + Long.parseLong(args[2].substring(1)));
		else
			throw new CmdSyntaxError("不是长类型数字: " + args[2]);
	}
}
