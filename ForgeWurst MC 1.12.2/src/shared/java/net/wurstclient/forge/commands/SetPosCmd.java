/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.forge.Command;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.utils.MathUtils;

public final class SetPosCmd extends Command {
	public SetPosCmd() {
		super("setpos", "让你能传送到任意位置(高精度).",
				"语法: .setpos <X> <Y> <Z>");
	}

	@Override
	public void call(String[] args) throws CmdException {
		if (args.length != 3)
			throw new CmdSyntaxError();

		if (!MathUtils.isInteger(args[0]) && !MathUtils.isInteger(args[1]) && !MathUtils.isInteger(args[2]) )
			throw new CmdSyntaxError();

        BigInteger posX = BigInteger.valueOf(args[0]);
        BigInteger posY = BigInteger.valueOf(args[1]);
        BigInteger posZ = BigInteger.valueOf(args[2]);

		EntityPlayerSP player = WMinecraft.getPlayer();
		player.setPosition(posX, posY, posZ);

        // 使用 BigInteger 来避免用户输入大于 IntMax .
	}
}
