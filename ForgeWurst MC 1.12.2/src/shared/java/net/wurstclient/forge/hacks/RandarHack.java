/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 * Modified by KrisTHL181 in 2025
 * 
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;


import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class RandarHack extends Hack
{
    private final SliderSetting seedSlider =
		new SliderSetting("世界种子", -4172144997902289642L, -9223372036854775808L, 9223372036854775807L, 1, ValueDisplay.INTEGER);
    private final SliderSetting searchingRange =
		new SliderSetting("RNG演化次数", "限制RNG搜索器演化的最大次数. \n默认值5000是一个经验值: 太大的参数可能使得游戏缓慢, 过小的值可能无法找到目标.\n见https://github.com/spawnmason/randar-explanation了解具体原理.", 5000, 500, 10000, 1, ValueDisplay.INTEGER);
    // SliderSetting是用的double存的数字, 但我这边得存long... 还不明白怎么搞成long类型的时候也不破坏现有代码，等什么时候有头绪来再往HackList注册这个东西

	public RandarHack()
	{
		super("RNG雷达", "让你能够通过挖掘方块定位他人坐标.\n请使用'.setslider RNG雷达 世界种子 <种子>'设定服务器种子而非滑动滑块.");
		setCategory(Category.MOVEMENT);
        addSetting(seedSlider);
        addSetting(searchingRange);
	}

	@Override
	protected void onEnable(){
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	protected void onDisable()
	{
		MinecraftForge.EVENT_BUS.unregister(this);
	}
	
    @SubscribeEvent
    public void onTossItem(ItemTossEvent event) {
        Entity itemEntity = event.getEntity();
        crackItemDropCoordinate(itemEntity.getPosition().getX(), itemEntity.getPosition().getY(), itemEntity.getPosition().getZ());
    }

	public void crackItemDropCoordinate(double dropX, double dropY, double dropZ) {
        float spawnX = ((float) (dropX - (int) Math.floor(dropX) - 0.25d)) * 2;
        float spawnY = ((float) (dropY - (int) Math.floor(dropY) - 0.25d)) * 2;
        float spawnZ = ((float) (dropZ - (int) Math.floor(dropZ) - 0.25d)) * 2;
        if (spawnX <= 0 || spawnX >= 1 || spawnY <= 0 || spawnY >= 1 || spawnZ <= 0 || spawnZ >= 1) {
            ChatUtils.warning("跳过此物品, 因为其坐标超出范围. 这可能意味着该物品只是偶然地看起来像是从挖掘方块时掉落的物品. 其他放置物品的方式(例如从玩家的物品栏中放置)有时会导致类似的误报.");
            return;
        }
        int measurement1 = (int) (spawnX * (1 << 24));
        int measurement2 = (int) (spawnY * (1 << 24));
        int measurement3 = (int) (spawnZ * (1 << 24));
        long cubeCenterX = ((long) measurement1 << 24) + 8388608L;
        long cubeCenterY = ((long) measurement2 << 24) + 8388597L;
        long cubeCenterZ = ((long) measurement3 << 24) - 277355554490L;
        double basisCoeff0 = 9.555378710501827E-11 * cubeCenterX + -2.5481838861196593E-10 * cubeCenterY + 1.184083942007419E-10 * cubeCenterZ;
        double basisCoeff1 = -1.2602185961441137E-10 * cubeCenterX + 6.980727107475104E-11 * cubeCenterY + 1.5362999761237006E-10 * cubeCenterZ;
        double basisCoeff2 = -1.5485213111787743E-10 * cubeCenterX + -1.2997958265259513E-10 * cubeCenterY + -5.6285642813236336E-11 * cubeCenterZ;
        long seed = Math.round(basisCoeff0) * 1270789291L + Math.round(basisCoeff1) * -2355713969L + Math.round(basisCoeff2) * -3756485696L & 281474976710655L;
        long next = seed * 25214903917L + 11L & 281474976710655L;
        long nextNext = next * 25214903917L + 11L & 281474976710655L;
        if ((seed >> 24 ^ measurement1 | next >> 24 ^ measurement2 | nextNext >> 24 ^ measurement3) != 0L) {
            ChatUtils.warning("跳过此物品, 因为其坐标超出范围. 这可能意味着该物品只是偶然地看起来像是从挖掘方块时掉落的物品. 其他放置物品的方式(例如从玩家的物品栏中放置)有时会导致类似的误报.");
            return;
        }
        long origSeed = seed;
        for (int i = 0; i < searchingRange.getValueI(); i++) {
            for (int x = -23440; x <= 23440; x++) {
                long z = (((seed ^ 25214903917L) - (long)seedSlider.getValue() - 10387319 - x * 341873128712L) * 211541297333629L) << 16 >> 16;
                if (z >= -23440 && z <= 23440) {
                    System.out.println("物品掉落在: " + dropX + " " + dropY + " " + dropZ);
                    System.out.println("因此, RNG测量是: " + measurement1 + " " + measurement2 + " " + measurement3);
                    System.out.println("这表明 java.util.Random 的内部种子可能是 " + origSeed);
                    System.out.println("在林地区域找到了一个森林匹配项: X:" + x + " Z:" + z + " 这可能设置了种子为:" + seed);
                    ChatUtils.message("定位到某玩家在: X:" + (x * 1280 - 128) + " , Z:" + (z * 1280 - 128) + " 和 X:" + (x * 1280 + 1151) + " , Z:" + (z * 1280 + 1151) + " 之间.");
                    return;
                }
            }
            seed = (seed * 246154705703781L + 107048004364969L) & 281474976710655L;
        }
        ChatUtils.error("破解失败. 这可能因为你的'世界种子'设置错误, 或者最近没有加载区块.");
    }
}
