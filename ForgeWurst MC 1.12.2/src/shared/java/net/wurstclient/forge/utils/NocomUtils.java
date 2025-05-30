/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 * Modified By KrisTHL181 in 2025 for Synchronous Block Detection
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */

package net.wurstclient.forge.utils;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;

public final class NocomUtils {
    // --- Static state fields for the synchronous probe ---
    private static volatile boolean S_isProbingGlobally = false; // Global lock to prevent concurrent probes
    private static BlockPos S_currentTargetPos = null;
    private static volatile IBlockState S_probeResultState = null;
    private static volatile boolean S_probePacketReceived = false; // Flag set by listener

    private static final long PROBE_TIMEOUT_MS = 2000; // Max time to wait (e.g., 2 seconds)

    // Listener instances
    private static final BlockProbePacketListener S_LISTENER_INSTANCE = new BlockProbePacketListener();
    private static boolean S_listenersRegistered = false;

    private static void ensureListenersRegistered() {
        // This should ideally be called once during client/mod initialization
        if (!S_listenersRegistered) {
            MinecraftForge.EVENT_BUS.register(S_LISTENER_INSTANCE);
            S_listenersRegistered = true;
        }
    }

    /**
     * Attempts to synchronously probe a block at the given coordinates using NoCom logic.
     * WARNING: This function will block and freeze the client until a result or timeout.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param z The z-coordinate.
     * @param facingOverride Optional EnumFacing to use for the probe. If null, EnumFacing.DOWN is used.
     * @return The IBlockState of the block if detected, or null on failure/timeout.
     */
    @Nullable
    public static IBlockState getBlockStateViaNoCom(long x, long y, long z, @Nullable EnumFacing facingOverride) {
        ensureListenersRegistered(); // Ensure listeners are active

        if (S_isProbingGlobally) {
            // Log to console or handle as an error; avoid ChatUtils here as per function design.
            System.err.println("NoComUtils.getBlockStateViaNoCom: Probe attempt while another is already in progress.");
            return null; 
        }

        NetHandlerPlayClient networkHandler = Minecraft.getMinecraft().getConnection();
        if (networkHandler == null) {
            System.err.println("NoComUtils.getBlockStateViaNoCom: Not connected to a server.");
            return null;
        }

        S_isProbingGlobally = true;
        S_currentTargetPos = new BlockPos(x, y, z);
        S_probeResultState = null; // Reset from previous call
        S_probePacketReceived = false; // Reset flag

        EnumFacing facingToUse = (facingOverride != null) ? facingOverride : EnumFacing.DOWN;

        CPacketPlayerTryUseItemOnBlock packet = new CPacketPlayerTryUseItemOnBlock(
                S_currentTargetPos, facingToUse, EnumHand.MAIN_HAND, 0.5F, 0.5F, 0.5F);
        networkHandler.sendPacket(packet);

        long startTime = System.currentTimeMillis();
        try {
            while (!S_probePacketReceived && (System.currentTimeMillis() - startTime < PROBE_TIMEOUT_MS)) {
                try {
                    // Sleep briefly to yield the thread, allowing network events to be processed.
                    // This is crucial for the packet listener to receive the packet.
                    Thread.sleep(20); // Poll every 20ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupt status
                    System.err.println("NoComUtils.getBlockStateViaNoCom: Probe wait interrupted.");
                    return null; // Exit if interrupted
                }
            }
        } finally {
            // Cleanup state regardless of how the loop exits
            S_isProbingGlobally = false;
            S_currentTargetPos = null; 
            // S_probeResultState retains its value (either the detected state or null)
        }
        
        if (!S_probePacketReceived) {
            System.err.println("NoComUtils.getBlockStateViaNoCom: Probe timed out for " + x + "," + y + "," + z);
            // S_probeResultState will be null
        }

        return S_probeResultState; // This is the state set by the listener, or null
    }

    // ----- Listener Class -----
    // This listener directly supports the getBlockStateViaNoCom function
    public static class BlockProbePacketListener {
        @SubscribeEvent
        public void onPacketInput(WPacketInputEvent event) {
            // Check if we are actively probing and if the packet matches the target
            if (!S_isProbingGlobally || S_currentTargetPos == null || S_probePacketReceived) {
                return; // Not probing, no target, or already got a result for this probe
            }
            
            if (event.getPacket() instanceof SPacketBlockChange) {
                SPacketBlockChange blockChangePacket = (SPacketBlockChange) event.getPacket();
                if (S_currentTargetPos.equals(blockChangePacket.getBlockPosition())) {
                    S_probeResultState = blockChangePacket.getBlockState(); // Store the result
                    S_probePacketReceived = true; // Signal that we received the relevant packet
                    // No ChatUtils messages here; the calling function handles reporting.
                }
            }
            // No need to explicitly handle SPacketChunkData for this specific function's goal.
            // If SPacketBlockChange is not received, it will time out and return null.
        }
    }
    // The TickHandler for timeout is removed as the primary timeout logic
    // is now handled by the blocking loop within getBlockStateViaNoCom.
    // S_isProbingGlobally acts as the lock.
}