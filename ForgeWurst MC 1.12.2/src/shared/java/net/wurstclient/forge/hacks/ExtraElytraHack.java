/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 * Modified by KrisTHL181 in 2025
 * 
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt   
 */
package net.wurstclient.forge.hacks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.text.TextComponentString;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.network.play.client.CPacketEntityAction;

public final class ExtraElytraHack extends Hack
{
    private final CheckboxSetting instantFly = new CheckboxSetting(
        "瞬间飞行", "单次跳跃即可飞行, 不再需要双击空格了.", true);
    
    private final CheckboxSetting speedCtrl = new CheckboxSetting(
        "速度控制", "是否允许使用前进/后退键控制飞行速度.\n"
            + "默认: W和S\n" + "不需要烟花火箭!",
        true);
    
    private final CheckboxSetting heightCtrl =
        new CheckboxSetting("高度控制",
            "是否允许使用跳跃/蹲键控制飞行高度.\n"
                + "默认: 空格和Shift\n" + "不需要烟花火箭!",
            false);
    
    private final CheckboxSetting stopInWater =
        new CheckboxSetting("在水里停止飞行", true);
    
    private int jumpTimer;

    public ExtraElytraHack()
    {
        super("鞘翅加强", "让鞘翅变得更加好用.");
        setCategory(Category.MOVEMENT);
        addSetting(instantFly);
        addSetting(speedCtrl);
        addSetting(heightCtrl);
        addSetting(stopInWater);
    }
    
    @Override
    protected void onEnable()
    {
        MinecraftForge.EVENT_BUS.register(this);
        jumpTimer = 0;
    }
    
    @Override
    protected void onDisable()
    {
        MinecraftForge.EVENT_BUS.unregister(this);
    }
    
    @SubscribeEvent
    public void onUpdate(TickEvent.PlayerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END || mc.player == null)
            return;
        
        if(jumpTimer > 0)
            jumpTimer--;
        
        EntityPlayerSP player = mc.player;
        
        if(!player.isElytraFlying())
        {
            if(mc.gameSettings.keyBindJump.isPressed())
                doInstantFly();
            return;
        }
        
        if(stopInWater.isChecked() && player.isInWater())
        {
            sendStartStopPacket();
            return;
        }
        
        controlSpeed();
        controlHeight();
    }
    
    private void sendStartStopPacket()
    {
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
    }
    
    private void controlHeight()
    {
        if(!heightCtrl.isChecked())
            return;
        
        EntityPlayerSP player = mc.player;
        KeyBinding jump = mc.gameSettings.keyBindJump;
        KeyBinding sneak = mc.gameSettings.keyBindSneak;
        
        if(jump.isKeyDown())
            player.motionY += 0.08;
        else if(sneak.isKeyDown())
            player.motionY -= 0.04;
    }
    
    private void controlSpeed()
    {
        if(!speedCtrl.isChecked())
            return;
        
        EntityPlayerSP player = mc.player;
        float yaw = player.rotationYaw * 0.017453292F;
        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        
        if (forward != 0 || strafe != 0)
        {
            player.motionX += (-Math.sin(yaw) * 0.05) * forward;
            player.motionZ += (Math.cos(yaw) * 0.05) * forward;
        }
    }
    
    private void doInstantFly()
    {
        if(!instantFly.isChecked())
            return;
        
        EntityPlayerSP player = mc.player;
        
        if(jumpTimer <= 0)
        {
            jumpTimer = 20;
            player.motionY = 0.32;
            player.setSprinting(true);
        }
        
        sendStartStopPacket();
    }
}