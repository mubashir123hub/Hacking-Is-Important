package net.lavaclient.client.module.modules.combat;

import net.lavaclient.client.event.EventTarget;
import net.lavaclient.client.event.events.PacketEvent;
import net.lavaclient.client.module.Module;
import net.lavaclient.client.value.BoolValue;
import net.lavaclient.client.value.FloatValue;
import net.lavaclient.client.value.ListValue;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

/**
 * Enhanced Velocity module to reduce or cancel knockback
 */
public class Velocity extends Module {
    // Mode selector
    private final ListValue modeValue = addValue(new ListValue("Mode", new String[] {"Simple", "AAC", "Cancel", "Custom"}, "Simple"));
    
    // Horizontal and vertical reduction for Simple mode
    private final FloatValue horizontalValue = addValue(new FloatValue("Horizontal", 0.0f, 0.0f, 1.0f));
    private final FloatValue verticalValue = addValue(new FloatValue("Vertical", 0.0f, 0.0f, 1.0f));
    
    // AAC mode settings
    private final FloatValue aacReductionValue = addValue(new FloatValue("AAC-Reduction", 0.7f, 0.0f, 1.0f));
    
    // Custom mode settings
    private final FloatValue customXValue = addValue(new FloatValue("CustomX", 0.0f, -1.0f, 1.0f));
    private final FloatValue customYValue = addValue(new FloatValue("CustomY", 0.0f, -1.0f, 1.0f));
    private final FloatValue customZValue = addValue(new FloatValue("CustomZ", 0.0f, -1.0f, 1.0f));
    
    // Additional settings
    private final BoolValue combatOnlyValue = addValue(new BoolValue("CombatOnly", false));
    private final BoolValue explosionsValue = addValue(new BoolValue("Explosions", true));
    
    // Delayed velocity for AAC mode
    private boolean pendingVelocity = false;
    private long lastVelocity = 0L;
    
    /**
     * Constructor
     */
    public Velocity() {
        super("Velocity", "Modifies the knockback you take.", Category.COMBAT);
    }
    
    /**
     * Packet event handler
     * @param event Packet event
     */
    @EventTarget
    public void onPacket(PacketEvent event) {
        // Only handle packets in enabled state
        if (!getState()) {
            return;
        }
        
        // Only handle incoming packets
        if (!event.isIncoming()) {
            return;
        }
        
        // Check if we're in combat and combat only is enabled
        if (combatOnlyValue.get() && mc.player.hurtTime <= 0) {
            return;
        }
        
        // Handle entity velocity packets (knockback)
        if (event.getPacket() instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity packet = (SPacketEntityVelocity) event.getPacket();
            
            // Only handle packets for the player
            if (mc.world.getEntityByID(packet.getEntityID()) != mc.player) {
                return;
            }
            
            // Process velocity based on mode
            switch (modeValue.get()) {
                case "Simple":
                    // Simple percentage-based reduction
                    if (horizontalValue.get() == 0.0f && verticalValue.get() == 0.0f) {
                        event.setCancelled(true);
                    } else {
                        packet.motionX = (int) (packet.motionX * horizontalValue.get());
                        packet.motionY = (int) (packet.motionY * verticalValue.get());
                        packet.motionZ = (int) (packet.motionZ * horizontalValue.get());
                    }
                    break;
                    
                case "AAC":
                    // AAC mode uses timing to bypass anti-cheat
                    if (mc.player.hurtTime > 0) {
                        // Reduce knockback
                        packet.motionX = (int) (packet.motionX * aacReductionValue.get());
                        packet.motionY = (int) (packet.motionY * aacReductionValue.get());
                        packet.motionZ = (int) (packet.motionZ * aacReductionValue.get());
                        
                        // Set pending velocity
                        pendingVelocity = true;
                        lastVelocity = System.currentTimeMillis();
                    }
                    break;
                    
                case "Cancel":
                    // Simply cancel the packet
                    event.setCancelled(true);
                    break;
                    
                case "Custom":
                    // Custom multipliers for each axis
                    packet.motionX = (int) (packet.motionX * customXValue.get());
                    packet.motionY = (int) (packet.motionY * customYValue.get());
                    packet.motionZ = (int) (packet.motionZ * customZValue.get());
                    break;
            }
        }
        
        // Handle explosion packets if enabled
        if (explosionsValue.get() && event.getPacket() instanceof SPacketExplosion) {
            SPacketExplosion packet = (SPacketExplosion) event.getPacket();
            
            // Process explosion knockback based on mode
            switch (modeValue.get()) {
                case "Simple":
                    // Simple percentage-based reduction
                    if (horizontalValue.get() == 0.0f && verticalValue.get() == 0.0f) {
                        event.setCancelled(true);
                    } else {
                        packet.motionX *= horizontalValue.get();
                        packet.motionY *= verticalValue.get();
                        packet.motionZ *= horizontalValue.get();
                    }
                    break;
                    
                case "AAC":
                    // Reduce explosion knockback
                    packet.motionX *= aacReductionValue.get();
                    packet.motionY *= aacReductionValue.get();
                    packet.motionZ *= aacReductionValue.get();
                    break;
                    
                case "Cancel":
                    // Set all motion to zero
                    packet.motionX = 0;
                    packet.motionY = 0;
                    packet.motionZ = 0;
                    break;
                    
                case "Custom":
                    // Custom multipliers for each axis
                    packet.motionX *= customXValue.get();
                    packet.motionY *= customYValue.get();
                    packet.motionZ *= customZValue.get();
                    break;
            }
        }
    }
}
