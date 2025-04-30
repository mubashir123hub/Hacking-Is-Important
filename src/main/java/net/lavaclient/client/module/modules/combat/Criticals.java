package net.lavaclient.client.module.modules.combat;

import net.lavaclient.client.event.EventTarget;
import net.lavaclient.client.event.events.PacketEvent;
import net.lavaclient.client.module.Module;
import net.lavaclient.client.utils.MovementUtils;
import net.lavaclient.client.utils.TimerUtils;
import net.lavaclient.client.value.BoolValue;
import net.lavaclient.client.value.IntegerValue;
import net.lavaclient.client.value.ListValue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;

/**
 * Enhanced Criticals module to perform critical hits
 */
public class Criticals extends Module {
    // Mode selection
    private final ListValue modeValue = addValue(new ListValue("Mode", new String[] {"Packet", "Jump", "MiniJump", "NCP", "AAC", "Hover"}, "Packet"));
    
    // Delay between criticals
    private final IntegerValue delayValue = addValue(new IntegerValue("Delay", 0, 0, 1000));
    
    // Additional settings
    private final BoolValue hurtTimeCheck = addValue(new BoolValue("HurtTimeCheck", true));
    
    // Timer for delay
    private final TimerUtils timer = new TimerUtils();
    
    /**
     * Constructor
     */
    public Criticals() {
        super("Criticals", "Automatically perform critical hits.", Category.COMBAT);
    }
    
    /**
     * Packet event handler
     * @param event Packet event
     */
    @EventTarget
    public void onPacket(PacketEvent event) {
        // Only process outgoing packets
        if (!event.isOutgoing()) {
            return;
        }
        
        // Check if it's an attack packet
        if (event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            
            // Check if it's an attack action
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK) {
                // Check if the entity is a living entity
                if (!(packet.getEntityFromWorld(mc.world) instanceof EntityLivingBase)) {
                    return;
                }
                
                // Check player conditions
                if (!canCrit()) {
                    return;
                }
                
                // Check hurt time
                EntityLivingBase target = (EntityLivingBase) packet.getEntityFromWorld(mc.world);
                if (hurtTimeCheck.get() && target != null && target.hurtTime > 0) {
                    return;
                }
                
                // Check delay
                if (!timer.hasTimePassed(delayValue.get())) {
                    return;
                }
                
                // Reset timer
                timer.reset();
                
                // Perform critical based on mode
                switch (modeValue.get()) {
                    case "Packet":
                        // Packet-based critical (most reliable)
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + 0.0625,
                                mc.player.posZ,
                                false));
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY,
                                mc.player.posZ,
                                false));
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + 0.0125,
                                mc.player.posZ,
                                false));
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY,
                                mc.player.posZ,
                                false));
                        break;
                        
                    case "Jump":
                        // Actual jump (visible)
                        mc.player.jump();
                        break;
                        
                    case "MiniJump":
                        // Small jump
                        mc.player.motionY = 0.25;
                        break;
                        
                    case "NCP":
                        // NoCheatPlus bypass
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + 0.11,
                                mc.player.posZ,
                                false));
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + 0.1100013579,
                                mc.player.posZ,
                                false));
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + 0.0000013579,
                                mc.player.posZ,
                                false));
                        break;
                        
                    case "AAC":
                        // AAC 3.3.12 bypass
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + 0.042,
                                mc.player.posZ,
                                false));
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + 0.0099,
                                mc.player.posZ,
                                false));
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + 0.0099,
                                mc.player.posZ,
                                false));
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY,
                                mc.player.posZ,
                                false));
                        break;
                        
                    case "Hover":
                        // Hover critical
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + 0.0201,
                                mc.player.posZ,
                                false));
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + 0.03,
                                mc.player.posZ,
                                false));
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY,
                                mc.player.posZ,
                                false));
                        break;
                }
            }
        }
    }
    
    /**
     * Checks if we can perform a critical hit
     * @return Whether we can crit
     */
    private boolean canCrit() {
        // Check if player exists
        if (mc.player == null) {
            return false;
        }
        
        // Check basic conditions
        return mc.player.onGround && !mc.player.isInWater() && !mc.player.isInLava() 
                && !mc.player.isRiding() && !mc.player.isOnLadder() && !mc.player.isPotionActive(net.minecraft.init.MobEffects.BLINDNESS)
                && !MovementUtils.isMoving(); // Some modes work better when not moving
    }
}
