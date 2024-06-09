/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.wurstclient.forge.Command;
import net.wurstclient.forge.hacks.ClickGuiHack;
import net.wurstclient.forge.utils.ChatUtils;

public final class VrTweaksCmd extends Command {
	public VrTweaksCmd() {
		super("vrtweaks", "调整 ViveCraft 的设置.",
				"语法: .vrtweaks");
	}

	@Override
	public void call(String[] args) throws CmdException {
		if (args.length > 0)
			throw new CmdSyntaxError();

		ChatUtils.message("调整 VR 设置...");
		ClickGuiHack gui = wurst.getHax().clickGuiHack;

		if (gui.getMaxHeight() == 0)
			ChatUtils.message("滚动已被禁用.");
		else {
			gui.setMaxHeight(0);
			ChatUtils.message("滚动已被禁用.");
		}

		if (gui.isInventoryButton())
			ChatUtils.message("GUI按钮已开启.");
		else {
			gui.setInventoryButton(true);
			ChatUtils.message("GUI按钮已开启.");
		}

		ChatUtils.message("完成!");
	}
}
