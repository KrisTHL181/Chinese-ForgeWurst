/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge;

public enum Category {
	BLOCKS("方块"),
	MOVEMENT("移动"),
	COMBAT("战斗"),
	RENDER("渲染"),
	OP("OP挂")
	CHAT("聊天"),
	FUN("玩笑"),
	OTHER("其他");

	private final String name;

	private Category(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
