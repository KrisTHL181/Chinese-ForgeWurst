/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import java.lang.Double;

import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class RandarHack extends Hack
{
    // TODO: Set WORLD_SEED
    private static long WORLD_SEED = -4172144997902289642L; // change this for a server other than 2b2t
	public RandarHack()
	{
		super("RND雷达", "让你能够通过挖掘方块定位他人坐标.");
		setCategory(Category.MOVEMENT);
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
	public void onUpdate(WUpdateEvent event)
	{

	}
	public static void crackItemDropCoordinate(double dropX, double dropY, double dropZ) {
        float spawnX = ((float) (dropX - (int) Math.floor(dropX) - 0.25d)) * 2;
        float spawnY = ((float) (dropY - (int) Math.floor(dropY) - 0.25d)) * 2;
        float spawnZ = ((float) (dropZ - (int) Math.floor(dropZ) - 0.25d)) * 2;
        if (spawnX <= 0 || spawnX >= 1 || spawnY <= 0 || spawnY >= 1 || spawnZ <= 0 || spawnZ >= 1) {
            System.out.println("跳过此物品, 因为其坐标超出范围. 这可能意味着该物品只是偶然地看起来像是从挖掘方块时掉落的物品. 其他放置物品的方式(例如从玩家的物品栏中放置)有时会导致类似的误报.");
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
            System.out.println("跳过此物品, 因为其坐标超出范围. 这可能意味着该物品只是偶然地看起来像是从挖掘方块时掉落的物品. 其他放置物品的方式(例如从玩家的物品栏中放置)有时会导致类似的误报.");
            return;
        }
        long origSeed = seed;
        for (int i = 0; i < 5000; i++) {
            for (int x = -23440; x <= 23440; x++) {
                long z = (((seed ^ 25214903917L) - WORLD_SEED - 10387319 - x * 341873128712L) * 211541297333629L) << 16 >> 16;
                if (z >= -23440 && z <= 23440) {
                    System.out.println("物品掉落在: " + dropX + " " + dropY + " " + dropZ);
                    System.out.println("因此, RNG测量是: " + measurement1 + " " + measurement2 + " " + measurement3);
                    System.out.println("这表明 java.util.Random 的内部种子可能是 " + origSeed);
                    System.out.println("在林地区域找到了一个森林匹配项: X:" + x + " Z:" + z + " 这可能设置了种子为:" + seed);
                    System.out.println("定位到某人在: X:" + (x * 1280 - 128) + " , Z:" + (z * 1280 - 128) + " 和 X:" + (x * 1280 + 1151) + " , Z:" + (z * 1280 + 1151) + " 之间.");
                    return;
                }
            }
            seed = (seed * 246154705703781L + 107048004364969L) & 281474976710655L;
        }
        System.out.println("破解失败. 这可能因为你的'世界种子'设置错误, 或者最近没有加载区块.");
    }

    public static void receivedPacket(Packet<?> packet) { // call this for incoming packets
        if (packet instanceof SPacketSpawnObject) {
            SPacketSpawnObject obj = (SPacketSpawnObject) packet;
            if (obj.getType() == 2 && obj.getData() == 1 && obj.getSpeedY() == 1600) {
                new Thread(() -> crackItemDropCoordinate(obj.getX(), obj.getY(), obj.getZ())).start();
            }
        }
    }

}
