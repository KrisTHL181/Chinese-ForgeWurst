/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 * Modified by KrisTHL181 in 2025
 * 
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.wurstclient.forge.settings.CheckboxSetting;

public final class AutoEZHack extends Hack {
	private final static EnumSetting<Words> words = new EnumSetting<>("文本", Words.values(), Words.EZ);
	private final CheckboxSetting withClientName = new CheckboxSetting(
		"输出挂名", "自动宣传你使用的外挂.", false);

	public AutoEZHack() {
		super("自动嘲讽",
				"让你杀死其他玩家后自动嘲讽对方.");
		setCategory(Category.CHAT);
		addSetting(words);
		addSetting(withClientName);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onPlayerKill(LivingDeathEvent event) {
		if (event.getSource().getTrueSource() instanceof EntityPlayer
				&& event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer killer = (EntityPlayer) event.getSource().getTrueSource();
			EntityPlayer victim = (EntityPlayer) event.getEntityLiving();
			if (killer == WMinecraft.getPlayer()) {
				String sendWord = words.getSelected().word;
				sendWord = sendWord.replace("{name}", victim.getName());
				if (killer != null) {
					sendWord = sendWord.replace("{self}", killer.getName());
				} else {
					sendWord = sendWord.replace("{self}", "我");
				}
				if (this.withClientName.isChecked()){
					sendWord = sendWord.concat(" --Chinese ForgeWurst");
				}
				CPacketChatMessage chatMsg = new CPacketChatMessage(sendWord);
				mc.getConnection().sendPacket(chatMsg);
			}
		}
	}

	private enum Words {
		EZ("EZ", "EZ LOL"),
		FUN("笑死我了", "人机 {name} 笑死我了hhhh"),
		NOOB("菜就多练", "乐子 {name} 菜就多练"),
		JOKER("小丑", "小丑 {name} 被 {self} 打爆了!");

		private final String name;
		private final String word;

		private Words(String name, String word) {
			this.name = name;
			this.word = word;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
