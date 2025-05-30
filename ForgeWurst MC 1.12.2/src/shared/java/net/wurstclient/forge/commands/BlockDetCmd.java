/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 * Copyright (c) 2014-2022 Wurst-Imperium and contributors.
 * Modified By KrisTHL181 in 2025 for Block Detection Refactor
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands; // Assuming your commands are in this package

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent; // For timeout handling
import net.wurstclient.forge.Command; // Assuming Command base class is here
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.MathUtils;
import net.wurstclient.fmlevents.WPacketInputEvent; // Correct import for your event

public final class BlockDetCmd extends Command {

    // Static state fields for the probe
    private static BlockPos S_targetPos = null;
    private static boolean S_isProbing = false;
    private static long S_probeStartTime = 0;
    private static final long PROBE_TIMEOUT_MS = 3000; // 3 seconds timeout

    // Listener instances
    private static final BlockProbePacketListener S_LISTENER_INSTANCE = new BlockProbePacketListener();
    private static final TickHandler S_TICK_HANDLER_INSTANCE = new TickHandler(); // Made instance for clarity
    
    private static boolean S_listenersRegistered = false;


    public BlockDetCmd() {
        super("blockdet", "探测某一坐标的方块 (Uses NoCom logic).", "语法: .blockdet <x> <y> <z> [facing]");
        
        // Register listeners ONCE. This constructor might be called multiple times
        // depending on Wurst's command registration. Ideally, register in client setup.
        // For this structure, we ensure they are registered if not already.
        if (!S_listenersRegistered) {
            MinecraftForge.EVENT_BUS.register(S_LISTENER_INSTANCE);
            MinecraftForge.EVENT_BUS.register(S_TICK_HANDLER_INSTANCE);
            S_listenersRegistered = true;
        }
    }

    @Override
    public void call(String[] args) throws CmdException {
        // 1. Argument parsing & validation
        if (args.length < 3 || args.length > 4) {
            ChatUtils.error("需要3或4个参数: <x> <y> <z> [facing]"); // Message in call()
            throw new CmdSyntaxError(); // Throwing error is fine
        }
        if (!(MathUtils.isLong(args[0]) && MathUtils.isLong(args[1]) && MathUtils.isLong(args[2]))) {
            ChatUtils.error("错误的坐标格式: " + args[0] + " " + args[1] + " " + args[2]); // Message in call()
            throw new CmdSyntaxError();
        }

        // 2. Check if already probing
        if (S_isProbing) {
            ChatUtils.warning("先前的探测仍在进行中。请稍候。"); // Message in call()
            return;
        }

        long x = Long.parseLong(args[0]);
        long y = Long.parseLong(args[1]);
        long z = Long.parseLong(args[2]);
        BlockPos userTargetPos = new BlockPos(x, y, z);

        EnumFacing facing = EnumFacing.DOWN; // Default facing
        if (args.length == 4) {
            try {
                facing = EnumFacing.valueOf(args[3].toUpperCase());
            } catch (IllegalArgumentException e) {
                ChatUtils.error("无效的朝向: " + args[3] + ". 使用 UP, DOWN, NORTH, SOUTH, EAST, 或 WEST."); // Message in call()
                return;
            }
        }

        // 3. Get networkHandler
        NetHandlerPlayClient networkHandler = Minecraft.getMinecraft().getConnection();
        if (networkHandler == null) {
            ChatUtils.error("未连接到服务器。"); // Message in call()
            return;
        }
        
        // 4. Call the public static method to execute the probe action
        executeProbeAction(userTargetPos, facing, networkHandler);
        
        // 5. If execution reaches here, probe initiation was successful (no early exit)
        // Display initial messages to the user
        ChatUtils.message("已发送方块探测数据包至 " + x + ", " + y + ", " + z + " (朝向 " + facing.getName() + "). 等待服务器响应...");
        ChatUtils.message("如果探测到方块，将会显示一条信息。");
        ChatUtils.message("如果超时（" + (PROBE_TIMEOUT_MS / 1000) + " 秒）未收到特定响应，则假定为空气或未加载。");
    }

    /**
     * Executes the core logic for initiating a block probe.
     * Sets the state variables and sends the necessary packet.
     * This method does not perform any user messaging.
     *
     * @param target The BlockPos to probe.
     * @param facing The EnumFacing side of the block to interact with.
     * @param networkHandler The client's network handler.
     */
    public static void executeProbeAction(BlockPos target, EnumFacing facing, NetHandlerPlayClient networkHandler) {
        // Set static state fields
        S_targetPos = target; 
        S_isProbing = true;
        S_probeStartTime = System.currentTimeMillis();

        // Create and send the packet
        CPacketPlayerDigging packet = new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, S_targetPos, facing);
        networkHandler.sendPacket(packet);
    }

    // ----- Listener Classes -----
    // These listeners will still use ChatUtils for their asynchronous result messages.

    public static class BlockProbePacketListener {
        @SubscribeEvent
        public void onPacketInput(WPacketInputEvent event) {
            if (!S_isProbing || S_targetPos == null) {
                return;
            }
            
            if (event.getPacket() instanceof SPacketBlockChange) {
                SPacketBlockChange blockChangePacket = (SPacketBlockChange) event.getPacket();
                if (S_targetPos.equals(blockChangePacket.getBlockPosition())) {
                    ChatUtils.message("§a方块探测成功!§r"); // Asynchronous result message
                    ChatUtils.message("坐标: " + blockChangePacket.getBlockPosition().getX() + ", " +
                                      blockChangePacket.getBlockPosition().getY() + ", " +
                                      blockChangePacket.getBlockPosition().getZ());
                    ChatUtils.message("方块状态: " + blockChangePacket.getBlockState());
                    resetProbeState();
                }
            } else if (event.getPacket() instanceof SPacketChunkData) {
                SPacketChunkData chunkPacket = (SPacketChunkData) event.getPacket();
                if (S_targetPos.getX() >> 4 == chunkPacket.getChunkX() && S_targetPos.getZ() >> 4 == chunkPacket.getChunkZ()) {
                    // This is an informational event, not a direct confirmation/denial of the specific block.
                    // No direct ChatUtils message here unless for debugging, as it can be noisy.
                    // The timeout or SPacketBlockChange are more definitive.
                }
            }
        }
    }
    
    public static class TickHandler {
        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) { 
                if (S_isProbing && S_targetPos != null && System.currentTimeMillis() > S_probeStartTime + PROBE_TIMEOUT_MS) {
                    handleTimeout();
                }
            }
        }
    }

    // ----- Helper Methods for State Management -----

    private static void handleTimeout() {
        if (S_isProbing) { 
            ChatUtils.warning("探测超时。在 " + (S_targetPos != null ? S_targetPos.getX() + ", " + S_targetPos.getY() + ", " + S_targetPos.getZ() : "未知位置") + " 未收到特定的方块变更响应。"); // Asynchronous result message
            ChatUtils.message("这可能意味着该位置是空气，或者区块未被服务器加载以进行交互，或者服务器没有以预期的方式响应。");
            resetProbeState();
        }
    }
    
    private static void resetProbeState(){
        S_isProbing = false;
        S_targetPos = null;
    }
}