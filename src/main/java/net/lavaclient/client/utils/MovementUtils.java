package net.lavaclient.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Utility class for movement related operations
 */
public class MovementUtils {
    // Minecraft instance
    private static final Minecraft mc = Minecraft.getMinecraft();
    
    /**
     * Checks if the player is moving
     * @return Whether the player is moving
     */
    public static boolean isMoving() {
        return mc.player != null && (mc.player.movementInput.moveForward != 0f || mc.player.movementInput.moveStrafe != 0f);
    }
    
    /**
     * Gets the player's movement speed
     * @return Movement speed
     */
    public static double getSpeed() {
        if (mc.player == null) {
            return 0.0;
        }
        
        return Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
    }
    
    /**
     * Applies a strafe to the player's motion
     * @param speed Speed multiplier
     */
    public static void strafe(float speed) {
        if (mc.player == null || !isMoving()) {
            return;
        }
        
        double yaw = getDirection();
        mc.player.motionX = -Math.sin(yaw) * speed;
        mc.player.motionZ = Math.cos(yaw) * speed;
    }
    
    /**
     * Applies a forward motion
     * @param speed Speed multiplier
     */
    public static void forward(float speed) {
        if (mc.player == null) {
            return;
        }
        
        double yaw = Math.toRadians(mc.player.rotationYaw);
        mc.player.motionX = -Math.sin(yaw) * speed;
        mc.player.motionZ = Math.cos(yaw) * speed;
    }
    
    /**
     * Gets the player's movement direction
     * @return Direction in radians
     */
    public static double getDirection() {
        if (mc.player == null) {
            return 0.0;
        }
        
        float moveForward = mc.player.movementInput.moveForward;
        float moveStrafe = mc.player.movementInput.moveStrafe;
        float rotationYaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        
        if (moveForward != 0) {
            if (moveStrafe > 0) {
                rotationYaw += (moveForward > 0 ? -45 : 45);
            } else if (moveStrafe < 0) {
                rotationYaw += (moveForward > 0 ? 45 : -45);
            }
            
            moveStrafe = 0;
            
            if (moveForward > 0) {
                moveForward = 1;
            } else {
                moveForward = -1;
            }
        }
        
        double direction = Math.toRadians(rotationYaw - 90);
        
        if (moveStrafe != 0 && moveForward == 0) {
            direction += (Math.PI / 2) * (moveStrafe > 0 ? -1 : 1);
        }
        
        return direction;
    }
    
    /**
     * Calculates the jump motion based on the player's parameters
     * @param motionY Current Y motion
     * @return Jump motion
     */
    public static double getJumpMotion(double motionY) {
        if (mc.player == null) {
            return 0.0;
        }
        
        // Standard jump height
        double motion = 0.42;
        
        // Check for jump boost potion effect
        if (mc.player.isPotionActive(net.minecraft.init.MobEffects.JUMP_BOOST)) {
            motion += (mc.player.getActivePotionEffect(net.minecraft.init.MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1;
        }
        
        return motion;
    }
    
    /**
     * Calculates the distance between two entities
     * @param entity1 First entity
     * @param entity2 Second entity
     * @return Distance
     */
    public static double getDistanceBetween(Entity entity1, Entity entity2) {
        return entity1.getDistance(entity2);
    }
    
    /**
     * Gets a normalized movement input vector
     * @return Normalized movement vector
     */
    public static Vec3d getMoveVector() {
        if (mc.player == null) {
            return new Vec3d(0, 0, 0);
        }
        
        double x = mc.player.movementInput.moveForward;
        double z = mc.player.movementInput.moveStrafe;
        
        // Normalize the vector if needed
        double magnitude = Math.sqrt(x * x + z * z);
        if (magnitude > 1.0) {
            x /= magnitude;
            z /= magnitude;
        }
        
        return new Vec3d(x, 0, z);
    }
    
    /**
     * Sets the player's position with safeguards
     * @param player The player
     * @param x X position
     * @param y Y position
     * @param z Z position
     */
    public static void setPosition(EntityPlayer player, double x, double y, double z) {
        if (player == null) {
            return;
        }
        
        player.setPosition(x, y, z);
    }
    
    /**
     * Calculates the base speed with potion effects
     * @return Base speed
     */
    public static double getBaseMoveSpeed() {
        if (mc.player == null) {
            return 0.0;
        }
        
        double baseSpeed = 0.2873;
        
        // Check for speed potion effect
        if (mc.player.isPotionActive(net.minecraft.init.MobEffects.SPEED)) {
            int amplifier = mc.player.getActivePotionEffect(net.minecraft.init.MobEffects.SPEED).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        
        return baseSpeed;
    }
    
    /**
     * Sets the player's speed
     * @param speed Speed value
     */
    public static void setSpeed(double speed) {
        if (mc.player == null || !isMoving()) {
            return;
        }
        
        double yaw = getDirection();
        mc.player.motionX = -Math.sin(yaw) * speed;
        mc.player.motionZ = Math.cos(yaw) * speed;
    }
    
    /**
     * Calculates the hypoteneuse length from X and Z components
     * @param x X component
     * @param z Z component
     * @return Hypoteneuse length
     */
    public static double pythagoras(double x, double z) {
        return Math.sqrt(x * x + z * z);
    }
}
