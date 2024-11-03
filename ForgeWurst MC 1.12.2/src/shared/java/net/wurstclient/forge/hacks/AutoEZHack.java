/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
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
import net.wurstclient.forge.utils.ChatUtils;
import net.minecraftforge.event.entity.living.LivingDeathEvent;


public final class AutoEZHack extends Hack
{
    private final EnumSetting<Words> word =
		new EnumSetting<>("样式", Words.values(), Words.EZ);

	public AutoEZHack()
	{
		super("自动嘲讽",
			"让你杀死其他玩家后自动嘲讽对方.");
		setCategory(Category.CHAT);
	}
	
	@Override
	protected void onEnable()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	protected void onDisable()
	{
		MinecraftForge.EVENT_BUS.unregister(this);
	}

    @SubscribeEvent
    public static void onPlayerKill(LivingDeathEvent event) {
        if (event.getSource().getTrueSource() instanceof PlayerEntity && event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity killer = (PlayerEntity) event.getSource().getTrueSource();
            PlayerEntity victim = (PlayerEntity) event.getEntityLiving();
            if (killer = WMinecraft.getPlayer()){
                String sendWord = word.getSelected().word;
                word = sendWord.replace("{name}", victim.getName());
                word = sendWord.replace("{self}", killer.getName());
                CPacketChatMessage chatMsg = new CPacketChatMessage(sendWord);
                WMinecraft.getMinecraft().getConnection().sendPacket(chatMsg);
            }
        }
    }

	private enum Words
	{
		EZ("EZ", "EZ LOL"),
		FUN("笑死我了", "人机 {name} 笑死我了hhhh"),
		NOOB("菜就多练", "乐子 {name} 菜就多练"),
        JOKER("小丑", "小丑 {name} 被 {self} 打爆了!");

		private final String name;
		private final String word;

		private Words(String name, String word;)
		{
			this.name = name;
			self.word = word
		}

		@Override
		public String toString()
		{
			return name;
		}
	}
}