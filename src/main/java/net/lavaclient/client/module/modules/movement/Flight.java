package net.lavaclient.client.module.modules.movement;

import net.lavaclient.client.event.EventTarget;
import net.lavaclient.client.event.events.MotionEvent;
import net.lavaclient.client.event.events.PacketEvent;
import net.lavaclient.client.event.events.UpdateEvent;
import net.lavaclient.client.module.Module;
import net.lavaclient.client.utils.MovementUtils;
import net.lavaclient.client.utils.TimerUtils;
import net.lavaclient.client.value.BoolValue;
import net.lavaclient.client.value.FloatValue;
import net.lavaclient.client.value.IntegerValue;
import net.lavaclient.client.value.ListValue;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;

/**
 * Enhanced Flight module with multiple modes
 */
public class Flight extends Module {
    // Mode selection
    private final ListValue modeValue = addValue(new ListValue("Mode", new String[] {
            "Vanilla", "Creative", "Glide", "AAC", "Hypixel", "Zonecraft", "Sentinel", "Minemora"
    }, "Vanilla"));
    
    // Vanilla mode settings
    private final FloatValue vanillaSpeedValue = addValue(new FloatValue("VanillaSpeed", 2.0f, 0.1f, 5.0f));
    
    // Glide mode settings
    private final FloatValue glideSpeedValue = addValue(new FloatValue("GlideSpeed", 0.3f, 0.1f, 1.0f));
    private final FloatValue glideDropValue = addValue(new FloatValue("GlideDrop", 0.1f, 0.0f, 0.2f));
    
    // AAC mode settings
    private final FloatValue aacSpeedValue = addValue(new FloatValue("AACSpeed", 0.3f, 0.1f, 1.0f));
    private final BoolValue aacFastValue = addValue(new BoolValue("AACFast", false));
    
    // Hypixel mode settings
    private final BoolValue hypixelBoostValue = addValue(new BoolValue("HypixelBoost", true));
    private final IntegerValue hypixelBoostDelayValue = addValue(new IntegerValue("HypixelBoostDelay", 1200, 0, 2000));
    private final FloatValue hypixelBoostTimerValue = addValue(new FloatValue("HypixelBoostTimer", 1.0f, 0.0f, 3.0f));
    
    // General settings
    private final BoolValue strafeValue = addValue(new BoolValue("Strafe", true));
    private final BoolValue noClipValue = addValue(new BoolValue("NoClip", false));
    private final BoolValue markValue = addValue(new BoolValue("Mark", false));
    private final BoolValue antiKickValue = addValue(new BoolValue("AntiKick", true));
    
    // Timers
    private final TimerUtils hypixelTimer = new TimerUtils();
    private final TimerUtils antiKickTimer = new TimerUtils();
    
    // State variables
    private double startY;
    private int boostTicks = 0;
    private boolean hypixelBoost = false;
    private double lastMotionX = 0;
    private double lastMotionZ = 0;
    
    /**
     * Constructor
     */
    public Flight() {
        super("Flight", "Allows you to fly.", Category.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        
        if (mc.player == null) {
            return;
        }
        
        // Initialize start height and state
        startY = mc.player.posY;
        boostTicks = 0;
        hypixelBoost = false;
        
        String mode = modeValue.get();
        
        // Special mode initializations
        if (mode.equals("Hypixel")) {
            // Start Timer for Hypixel mode
            hypixelTimer.reset();
        } else if (mode.equals("Creative")) {
            // Set creative flight mode
            mc.player.capabilities.isFlying = true;
            
            if (mc.player.capabilities.isCreativeMode) {
                return;
            }
            
            mc.player.capabilities.allowFlying = true;
        }
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        
        if (mc.player == null) {
            return;
        }
        
        // Cleanup mode specific changes
        String mode = modeValue.get();
        
        if (mode.equals("Creative")) {
            mc.player.capabilities.isFlying = false;
            mc.player.capabilities.allowFlying = mc.player.isCreative();
        }
        
        // Reset speed
        mc.player.capabilities.setFlySpeed(0.05f);
        
        // Reset clip state
        mc.player.noClip = false;
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
        
        // Apply NoClip if enabled
        if (noClipValue.get()) {
            mc.player.noClip = true;
        }
        
        // Handle different flight modes
        String mode = modeValue.get();
        
        switch (mode) {
            case "Vanilla":
                handleVanilla();
                break;
                
            case "Creative":
                // Handled in enable/disable
                mc.player.capabilities.setFlySpeed(vanillaSpeedValue.get() / 10f);
                break;
                
            case "Glide":
                handleGlide();
                break;
                
            case "AAC":
                handleAAC();
                break;
                
            case "Hypixel":
                handleHypixel(event);
                break;
                
            case "Zonecraft":
                handleZonecraft();
                break;
                
            case "Sentinel":
                handleSentinel();
                break;
                
            case "Minemora":
                handleMinemora();
                break;
        }
        
        // Apply anti-kick if enabled
        if (antiKickValue.get() && !mc.player.onGround) {
            if (antiKickTimer.hasTimePassed(1000)) {
                antiKickTimer.reset();
                // Small downward motion to prevent kick
                mc.player.motionY = -0.04;
            }
        }
    }
    
    /**
     * Update event handler
     * @param event Update event
     */
    @EventTarget
    public void onUpdate(UpdateEvent event) {
        // Mark current position if enabled
        if (markValue.get() && mc.player != null) {
            // In an actual implementation, we would render markers at the player's position
        }
    }
    
    /**
     * Packet event handler
     * @param event Packet event
     */
    @EventTarget
    public void onPacket(PacketEvent event) {
        if (mc.player == null) {
            return;
        }
        
        // Handle teleport packets (for anti-cheat evasion)
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            if (modeValue.get().equals("Hypixel") || modeValue.get().equals("AAC")) {
                // Accept teleport but maintain our speed
                lastMotionX = mc.player.motionX;
                lastMotionZ = mc.player.motionZ;
            }
        }
    }
    
    /**
     * Handles vanilla flight mode
     */
    private void handleVanilla() {
        // Set motion to 0 to prevent falling
        mc.player.motionY = 0;
        
        // Apply speed and strafe if moving
        if (strafeValue.get() && MovementUtils.isMoving()) {
            MovementUtils.strafe(vanillaSpeedValue.get());
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
            
            // Apply vertical speed on key press
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.player.motionY = vanillaSpeedValue.get();
            }
            
            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.player.motionY = -vanillaSpeedValue.get();
            }
            
            // Apply horizontal speed on key press
            if (mc.gameSettings.keyBindForward.isKeyDown()) {
                MovementUtils.forward(vanillaSpeedValue.get());
            }
        }
    }
    
    /**
     * Handles glide flight mode
     */
    private void handleGlide() {
        // Slow downward motion
        mc.player.motionY = -glideDropValue.get();
        
        // Apply horizontal movement
        if (MovementUtils.isMoving()) {
            MovementUtils.strafe(glideSpeedValue.get());
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
    }
    
    /**
     * Handles AAC flight mode
     */
    private void handleAAC() {
        // Basic upward motion
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY = aacSpeedValue.get();
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY = -aacSpeedValue.get();
        } else {
            mc.player.motionY = 0;
        }
        
        // AAC fast mode
        if (aacFastValue.get()) {
            if (mc.player.ticksExisted % 3 == 0) {
                mc.player.motionY = -0.03;
            }
        }
        
        // Horizontal movement
        if (MovementUtils.isMoving()) {
            MovementUtils.strafe(aacSpeedValue.get());
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
    }
    
    /**
     * Handles Hypixel flight mode
     * @param event Motion event
     */
    private void handleHypixel(MotionEvent event) {
        // Vertical motion controls
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY = 0.42;
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY = -0.42;
        } else {
            mc.player.motionY = 0;
        }
        
        // Handle boost timing
        if (hypixelBoostValue.get() && !hypixelBoost && hypixelTimer.hasTimePassed(hypixelBoostDelayValue.get())) {
            hypixelBoost = true;
            hypixelTimer.reset();
        }
        
        // Apply boost
        float speed = 0.25f;
        if (hypixelBoost) {
            if (boostTicks < 10) {
                speed = 1.7f;
                boostTicks++;
            } else {
                hypixelBoost = false;
                boostTicks = 0;
            }
        }
        
        // Apply movement
        if (MovementUtils.isMoving()) {
            MovementUtils.strafe(speed);
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
        
        // Disable after certain time to avoid detection
        if (event.isPre() && mc.player.ticksExisted % 40 == 0) {
            // Send ground status to avoid detection
            mc.player.onGround = true;
            mc.getConnection().sendPacket(new CPacketPlayer.Position(
                    mc.player.posX, mc.player.posY, mc.player.posZ, true));
        }
    }
    
    /**
     * Handles Zonecraft flight mode
     */
    private void handleZonecraft() {
        // Special motion patterns to bypass ZoneCraft anti-cheat
        if (mc.player.ticksExisted % 2 == 0) {
            mc.player.motionY = 0.04;
        } else {
            mc.player.motionY = -0.04;
        }
        
        // Horizontal movement
        float speed = 2.0f;
        if (mc.player.ticksExisted % 20 == 0) {
            // Lower speed periodically to avoid detection
            speed = 0.3f;
        }
        
        if (MovementUtils.isMoving()) {
            MovementUtils.strafe(speed);
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
    }
    
    /**
     * Handles Sentinel flight mode
     */
    private void handleSentinel() {
        // Simple vertical motion
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY = 1.0;
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY = -1.0;
        } else {
            mc.player.motionY = 0;
        }
        
        // Send ground packets every 10 ticks to avoid detection
        if (mc.player.ticksExisted % 10 == 0) {
            mc.player.onGround = true;
            mc.getConnection().sendPacket(new CPacketPlayer(true));
        }
        
        // Horizontal movement
        if (MovementUtils.isMoving()) {
            MovementUtils.strafe(0.5f);
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
    }
    
    /**
     * Handles Minemora flight mode
     */
    private void handleMinemora() {
        // Vertical control
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY = 0.5;
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY = -0.5;
        } else {
            // Minemora requires some vertical motion
            if (mc.player.ticksExisted % 20 < 10) {
                mc.player.motionY = 0.02;
            } else {
                mc.player.motionY = -0.02;
            }
        }
        
        // Horizontal movement
        float speed = 0.8f;
        
        if (mc.player.ticksExisted % 30 == 0) {
            // Brief pause in movement to avoid detection
            speed = 0.0f;
        }
        
        if (MovementUtils.isMoving()) {
            MovementUtils.strafe(speed);
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
    }
}
