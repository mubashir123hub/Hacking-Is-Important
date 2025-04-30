package net.lavaclient.client.module.modules.movement;

import net.lavaclient.client.event.EventTarget;
import net.lavaclient.client.event.events.MotionEvent;
import net.lavaclient.client.event.events.UpdateEvent;
import net.lavaclient.client.module.Module;
import net.lavaclient.client.utils.MovementUtils;
import net.lavaclient.client.utils.PlayerUtils;
import net.lavaclient.client.utils.TimerUtils;
import net.lavaclient.client.value.BoolValue;
import net.lavaclient.client.value.FloatValue;
import net.lavaclient.client.value.IntegerValue;
import net.lavaclient.client.value.ListValue;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.MathHelper;

/**
 * Enhanced Speed module with multiple modes
 */
public class Speed extends Module {
    // Mode selection
    private final ListValue modeValue = addValue(new ListValue("Mode", new String[] {
            "Vanilla", "Strafe", "YPort", "Bhop", "LowHop", "Matrix", "LegitHop"
    }, "Vanilla"));
    
    // Vanilla mode settings
    private final FloatValue vanillaSpeedValue = addValue(new FloatValue("VanillaSpeed", 0.5f, 0.1f, 2.0f));
    
    // Timer settings
    private final BoolValue useTimerValue = addValue(new BoolValue("UseTimer", false));
    private final FloatValue timerValue = addValue(new FloatValue("Timer", 1.1f, 0.1f, 2.0f));
    
    // Strafe settings
    private final FloatValue strafeSpeedValue = addValue(new FloatValue("StrafeSpeed", 0.27f, 0.1f, 0.6f));
    private final BoolValue strafeJumpValue = addValue(new BoolValue("StrafeJump", true));
    private final FloatValue strafeHeightValue = addValue(new FloatValue("StrafeHeight", 0.42f, 0.1f, 0.6f));
    
    // Bhop settings
    private final FloatValue bhopSpeedValue = addValue(new FloatValue("BhopSpeed", 0.25f, 0.1f, 0.5f));
    
    // YPort settings
    private final FloatValue yPortSpeedValue = addValue(new FloatValue("YPortSpeed", 0.25f, 0.1f, 0.5f));
    private final IntegerValue yPortLengthValue = addValue(new IntegerValue("YPortLength", 4, 1, 10));
    
    // LegitHop settings
    private final BoolValue legitHopPotionCheck = addValue(new BoolValue("LegitHopPotionCheck", true));
    
    // Timer for various operations
    private final TimerUtils timer = new TimerUtils();
    
    // State variables
    private int stage = 0;
    private double moveSpeed = 0.0;
    private double lastDist = 0.0;
    private boolean jumped = false;
    
    /**
     * Constructor
     */
    public Speed() {
        super("Speed", "Allows you to move faster.", Category.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        
        if (mc.player == null) {
            return;
        }
        
        // Reset state
        stage = 0;
        moveSpeed = getBaseMoveSpeed();
        lastDist = 0.0;
        jumped = false;
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        
        if (mc.player == null) {
            return;
        }
        
        // Reset timer if used
        if (useTimerValue.get()) {
            setTimer(1.0f);
        }
    }
    
    /**
     * Motion event handler
     * @param event Motion event
     */
    @EventTarget
    public void onMotion(MotionEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        
        // Apply timer if enabled
        if (useTimerValue.get()) {
            setTimer(timerValue.get());
        }
        
        // Handle different speed modes
        String mode = modeValue.get();
        
        switch (mode) {
            case "Vanilla":
                handleVanilla();
                break;
                
            case "Strafe":
                handleStrafe(event);
                break;
                
            case "YPort":
                handleYPort(event);
                break;
                
            case "Bhop":
                handleBhop(event);
                break;
                
            case "LowHop":
                handleLowHop(event);
                break;
                
            case "Matrix":
                handleMatrix(event);
                break;
                
            case "LegitHop":
                handleLegitHop(event);
                break;
        }
    }
    
    /**
     * Update event handler
     * @param event Update event
     */
    @EventTarget
    public void onUpdate(UpdateEvent event) {
        // Calculate last distance moved
        double xDist = mc.player.posX - mc.player.prevPosX;
        double zDist = mc.player.posZ - mc.player.prevPosZ;
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    }
    
    /**
     * Handles vanilla speed mode
     */
    private void handleVanilla() {
        // Simple speed multiplier
        MovementUtils.strafe(vanillaSpeedValue.get());
    }
    
    /**
     * Handles strafe speed mode
     * @param event Motion event
     */
    private void handleStrafe(MotionEvent event) {
        // Check if we're moving
        if (!MovementUtils.isMoving()) {
            return;
        }
        
        // Check ground state
        if (mc.player.onGround) {
            stage = 2;
        }
        
        if (stage == 1 && mc.player.moveForward != 0f) {
            // Stage 1: Set motion
            moveSpeed = 1.35 * getBaseMoveSpeed() - 0.01;
        } else if (stage == 2) {
            // Stage 2: Jump
            if (mc.player.onGround && strafeJumpValue.get()) {
                mc.player.motionY = strafeHeightValue.get();
                jumped = true;
            }
            
            // Increase speed on jump
            moveSpeed *= strafeSpeedValue.get();
        } else if (stage >= 3) {
            // Stage 3+: Gradually slow down
            moveSpeed = lastDist - lastDist / 159.0;
        }
        
        // Apply speed but don't drop below base speed
        moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
        
        // Apply movement
        MovementUtils.strafe(moveSpeed);
        
        // Increment stage
        stage++;
    }
    
    /**
     * Handles YPort speed mode
     * @param event Motion event
     */
    private void handleYPort(MotionEvent event) {
        // Only works when moving
        if (!MovementUtils.isMoving()) {
            return;
        }
        
        // Only on ground
        if (mc.player.onGround) {
            // Cycle through Y-port stages
            stage++;
            if (stage > yPortLengthValue.get()) {
                stage = 1;
            }
            
            if (stage == 1) {
                // First stage: Jump
                mc.player.motionY = 0.4;
                moveSpeed = yPortSpeedValue.get();
            } else {
                // Other stages: Y-port down
                mc.player.motionY = -0.18;
                moveSpeed = yPortSpeedValue.get() * 0.7;
            }
            
            // Apply movement
            MovementUtils.strafe(moveSpeed);
        }
    }
    
    /**
     * Handles bhop speed mode
     * @param event Motion event
     */
    private void handleBhop(MotionEvent event) {
        if (!MovementUtils.isMoving()) {
            return;
        }
        
        if (mc.player.onGround) {
            // Jump when on ground
            mc.player.jump();
            moveSpeed = bhopSpeedValue.get();
        } else {
            // Gradually slow down in air
            moveSpeed = Math.max(moveSpeed * 0.98, getBaseMoveSpeed());
        }
        
        // Apply movement
        MovementUtils.strafe(moveSpeed);
    }
    
    /**
     * Handles low hop speed mode
     * @param event Motion event
     */
    private void handleLowHop(MotionEvent event) {
        if (!MovementUtils.isMoving()) {
            return;
        }
        
        if (mc.player.onGround) {
            // Small jump
            mc.player.motionY = 0.3;
            moveSpeed = getBaseMoveSpeed() * 1.6;
        } else if (mc.player.motionY < 0) {
            // Force downward faster
            mc.player.motionY *= 1.2;
        }
        
        // Apply movement
        MovementUtils.strafe(moveSpeed);
    }
    
    /**
     * Handles Matrix speed mode
     * @param event Motion event
     */
    private void handleMatrix(MotionEvent event) {
        if (!MovementUtils.isMoving()) {
            return;
        }
        
        if (mc.player.onGround) {
            // Matrix bypass pattern
            if (stage % 3 == 0) {
                mc.player.motionY = 0.4;
                moveSpeed = getBaseMoveSpeed() * 1.5;
            } else {
                // Send ground packet to bypass anti-cheat
                mc.getConnection().sendPacket(new CPacketPlayer.Position(
                        mc.player.posX, mc.player.posY, mc.player.posZ, true));
                moveSpeed = getBaseMoveSpeed() * 1.2;
            }
            stage++;
        } else {
            moveSpeed = getBaseMoveSpeed();
        }
        
        // Apply movement
        MovementUtils.strafe(moveSpeed);
    }
    
    /**
     * Handles legitimate hop speed mode
     * @param event Motion event
     */
    private void handleLegitHop(MotionEvent event) {
        // This is designed to look legitimate
        if (!MovementUtils.isMoving()) {
            return;
        }
        
        // Check if we have speed potion
        boolean hasSpeedEffect = mc.player.isPotionActive(Potion.getPotionById(1));
        
        if (mc.player.onGround) {
            // Jump
            mc.player.jump();
            
            // Calculate legitimate speed
            if (legitHopPotionCheck.get() && hasSpeedEffect) {
                // Speed with potion feels legitimate
                moveSpeed = getBaseMoveSpeed() * 1.1;
            } else {
                // Normal speed that looks legitimate
                moveSpeed = getBaseMoveSpeed() * 1.05;
            }
        } else {
            // Normal air friction
            moveSpeed = Math.max(moveSpeed * 0.98, getBaseMoveSpeed());
        }
        
        // Apply movement naturally
        MovementUtils.strafe(moveSpeed);
    }
    
    /**
     * Gets the base movement speed
     * @return The base movement speed
     */
    private double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        
        // Factor in Speed potion effect
        if (mc.player.isPotionActive(Potion.getPotionById(1))) {
            int amplifier = mc.player.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        
        return baseSpeed;
    }
    
    /**
     * Sets the timer speed
     * @param speed The timer speed
     */
    private void setTimer(float speed) {
        try {
            // Access timer field via reflection
            // Note: This is just a stub, in a real implementation you'd need to
            // find the right field in the Minecraft class for timer manipulation
            
            // For safety, let's use a reasonable range
            speed = MathHelper.clamp(speed, 0.1f, 5.0f);
            
            // In an actual implementation, you would set the timer:
            // timer.timerSpeed = speed;
        } catch (Exception e) {
            System.out.println("Error setting timer: " + e.getMessage());
        }
    }
}
