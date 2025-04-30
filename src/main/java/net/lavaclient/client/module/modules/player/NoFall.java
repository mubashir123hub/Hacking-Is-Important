package net.lavaclient.client.module.modules.player;

import net.lavaclient.client.event.EventTarget;
import net.lavaclient.client.event.events.MotionEvent;
import net.lavaclient.client.event.events.PacketEvent;
import net.lavaclient.client.module.Module;
import net.lavaclient.client.value.BoolValue;
import net.lavaclient.client.value.ListValue;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

/**
 * Enhanced NoFall module to prevent fall damage
 */
public class NoFall extends Module {
    // Mode selection
    private final ListValue modeValue = addValue(new ListValue("Mode", new String[] {
            "Packet", "AAC", "NoGround", "SpoofGround", "Hypixel", "MLG", "Preserve"
    }, "Packet"));
    
    // Additional settings
    private final BoolValue minHeightValue = addValue(new BoolValue("MinHeight", true));
    private final BoolValue fallDistanceValue = addValue(new BoolValue("ResetFallDistance", true));
    
    // Internal state
    private boolean nextFallPacket = false;
    private int state = 0;
    
    /**
     * Constructor
     */
    public NoFall() {
        super("NoFall", "Prevents fall damage.", Category.PLAYER);
    }
    
    /**
     * Motion event handler
     * @param event Motion event
     */
    @EventTarget
    public void onMotion(MotionEvent event) {
        if (!event.isPre()) {
            return;
        }
        
        // Check if player is falling
        if (!mc.player.capabilities.isFlying && !mc.player.capabilities.disableDamage && 
            mc.player.fallDistance > 0 && (!minHeightValue.get() || mc.player.fallDistance > 3.0f)) {
            
            String mode = modeValue.get();
            
            switch (mode) {
                case "Packet":
                    // Simple packet-based method - most reliable
                    if (mc.player.fallDistance > 3.0f) {
                        mc.player.onGround = true;
                        mc.getConnection().sendPacket(new CPacketPlayer(true));
                    }
                    break;
                    
                case "AAC":
                    // AAC anti-cheat bypass
                    if (mc.player.fallDistance > 3.0f) {
                        mc.getConnection().sendPacket(new CPacketPlayer(true));
                        state = 2;
                    } else if (state == 2 && mc.player.fallDistance < 3.0f) {
                        mc.player.motionY = 0.1;
                        state = 3;
                        return;
                    }
                    
                    switch (state) {
                        case 3:
                            mc.player.motionY = 0.1;
                            state = 4;
                            break;
                        case 4:
                            mc.player.motionY = 0.1;
                            state = 5;
                            break;
                        case 5:
                            mc.player.motionY = 0.1;
                            state = 1;
                            break;
                    }
                    break;
                    
                case "NoGround":
                    // Always report not on ground
                    if (mc.player.fallDistance > 3.0f) {
                        event.setOnGround(false);
                        mc.player.onGround = false;
                    }
                    break;
                    
                case "SpoofGround":
                    // Always report on ground
                    if (mc.player.fallDistance > 3.0f) {
                        event.setOnGround(true);
                    }
                    break;
                    
                case "Hypixel":
                    // Hypixel-specific bypass
                    if (mc.player.fallDistance > 3.0f && nextFallPacket) {
                        event.setOnGround(true);
                        nextFallPacket = false;
                    } else {
                        nextFallPacket = true;
                    }
                    break;
                    
                case "MLG":
                    // MLG water bucket / cobweb placement simulation
                    if (mc.player.fallDistance > 3.0f) {
                        BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ);
                        
                        // Check if there's a block below that would break the fall
                        if (mc.world.getBlockState(blockPos).getMaterial().isLiquid() || 
                            mc.world.getBlockState(blockPos).getBlock().getRegistryName().toString().contains("web")) {
                            mc.player.fallDistance = 0f;
                        } else {
                            // Attempt to place water or other fall-breaking blocks would be here
                            // This is a simplified implementation
                        }
                    }
                    break;
                    
                case "Preserve":
                    // Preserves fall distance until safe
                    if (isSafeGround(mc.player.posY - 1.0) || 
                        isSafeGround(mc.player.posY - 2.0)) {
                        if (mc.player.fallDistance > 3.0f) {
                            event.setOnGround(true);
                            mc.player.fallDistance = 0f;
                        }
                    }
                    break;
            }
            
            // Reset fall distance if enabled
            if (fallDistanceValue.get() && mode.equals("Packet")) {
                mc.player.fallDistance = 0f;
            }
        }
    }
    
    /**
     * Packet event handler
     * @param event Packet event
     */
    @EventTarget
    public void onPacket(PacketEvent event) {
        if (!event.isOutgoing()) {
            return;
        }
        
        // Handle outgoing player packets
        if (event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();
            
            // Check fall distance
            if (mc.player.fallDistance > 3.0f && !mc.player.capabilities.isFlying && !mc.player.capabilities.disableDamage) {
                switch (modeValue.get()) {
                    case "Packet":
                        packet.onGround = true;
                        break;
                        
                    case "NoGround":
                        packet.onGround = false;
                        break;
                        
                    case "Hypixel":
                        if (nextFallPacket) {
                            packet.onGround = true;
                            nextFallPacket = false;
                        }
                        break;
                }
            }
        }
    }
    
    /**
     * Checks if a Y position has safe ground below
     * @param y The Y position
     * @return Whether it's safe to land
     */
    private boolean isSafeGround(double y) {
        BlockPos groundPos = new BlockPos(mc.player.posX, y, mc.player.posZ);
        
        // Check for liquids, which break falls
        if (mc.world.getBlockState(groundPos).getMaterial().isLiquid()) {
            return true;
        }
        
        // Check for slime, hay, bed, etc.
        String blockName = mc.world.getBlockState(groundPos).getBlock().getRegistryName().toString();
        return blockName.contains("slime") || 
               blockName.contains("hay") || 
               blockName.contains("bed") || 
               blockName.contains("web");
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        nextFallPacket = false;
        state = 0;
    }
}
