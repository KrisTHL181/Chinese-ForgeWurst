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

public final class WorldDupCmd extends Command {
	public WorldDupCmd() {
		super("worlddup", "让你可以通过一个已知种子生成'镜像种子'.",
				"语法: .worlddup <已知种子>");
	}

	@Override
	public void call(String[] args) throws CmdException {
        if (args.length != 1)
            throw new CmdSyntaxError();
		long originalSeed = Long.parseLong(args[0]);
		long posDupSeed = originalSeed + (1L << 48);
		long negDupSeed = originalSeed - (1L << 48);
		ChatUtils.message("镜像种子: " + posDupSeed + "\n镜像种子: " + negDupSeed);
		/*
			* "镜像种子"的定义: 每个Minecraft世界创建都有一个Seed种子, 在1.16.5前他都是通过内置的Random类来进行的.
			* 而内置的Random类采用的是LCG算法, 虽构造Random类时接受Long型(64位). 但是内部表示使用的状态仅有48位,
			* 种子会被Random构造时仅用掩码取出低48位. (因此, LCG的状态空间周期是2^48)
			* 因此，两个种子如果相差2^48(即 1L << 48), 它们在Random中会被视为等价. (模2^48同余)
			* 此时我们就可以通过原有种子加减1L << 48就可以制作一个"镜像种子",
			* 也就是除了种子之外, 两个世界的生成完全相同. --如果发生溢出, 他们将继续保持同余, 所以仍有效!
		*/
	}
}

