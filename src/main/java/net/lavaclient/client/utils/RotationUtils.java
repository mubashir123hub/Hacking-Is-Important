package net.lavaclient.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Utility class for rotation related operations
 */
public class RotationUtils {
    // Minecraft instance
    private static final Minecraft mc = Minecraft.getMinecraft();
    
    // Current rotation
    private static float targetYaw, targetPitch;
    private static boolean rotating = false;
    
    /**
     * Gets rotations to look at a target entity
     * @param entity Target entity
     * @return Yaw and pitch as array
     */
    public static float[] getRotations(Entity entity) {
        if (entity == null || mc.player == null) {
            return new float[] {mc.player.rotationYaw, mc.player.rotationPitch};
        }
        
        // Get positions
        double x = entity.posX - mc.player.posX;
        double y = entity.posY + entity.getEyeHeight() - (mc.player.posY + mc.player.getEyeHeight());
        double z = entity.posZ - mc.player.posZ;
        
        // Calculate distance
        double distance = Math.sqrt(x * x + z * z);
        
        // Calculate angles
        float yaw = (float) Math.toDegrees(-Math.atan2(x, z));
        float pitch = (float) Math.toDegrees(-Math.atan2(y, distance));
        
        return new float[] {
            MathHelper.wrapDegrees(yaw), 
            MathHelper.clamp(pitch, -90, 90)
        };
    }
    
    /**
     * Gets rotations to look at a position
     * @param pos Target position
     * @return Yaw and pitch as array
     */
    public static float[] getRotations(BlockPos pos) {
        return getRotations(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }
    
    /**
     * Gets rotations to look at a position
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return Yaw and pitch as array
     */
    public static float[] getRotations(double x, double y, double z) {
        if (mc.player == null) {
            return new float[] {0, 0};
        }
        
        // Get positions
        double xDiff = x - mc.player.posX;
        double yDiff = y - (mc.player.posY + mc.player.getEyeHeight());
        double zDiff = z - mc.player.posZ;
        
        // Calculate distance
        double distance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        
        // Calculate angles
        float yaw = (float) Math.toDegrees(-Math.atan2(xDiff, zDiff));
        float pitch = (float) Math.toDegrees(-Math.atan2(yDiff, distance));
        
        return new float[] {
            MathHelper.wrapDegrees(yaw), 
            MathHelper.clamp(pitch, -90, 90)
        };
    }
    
    /**
     * Gets rotations to look at a vector
     * @param vec Target vector
     * @return Yaw and pitch as array
     */
    public static float[] getRotations(Vec3d vec) {
        return getRotations(vec.x, vec.y, vec.z);
    }
    
    /**
     * Smoothly transitions between current and target rotations
     * @param currentYaw Current yaw
     * @param currentPitch Current pitch
     * @param targetYaw Target yaw
     * @param targetPitch Target pitch
     * @param smoothing Smoothing factor (higher = slower)
     * @return Smoothed rotations as array
     */
    public static float[] smoothRotation(float currentYaw, float currentPitch, float targetYaw, float targetPitch, float smoothing) {
        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float pitchDiff = targetPitch - currentPitch;
        
        float smoothedYaw = currentYaw + yawDiff / smoothing;
        float smoothedPitch = currentPitch + pitchDiff / smoothing;
        
        return new float[] {smoothedYaw, MathHelper.clamp(smoothedPitch, -90, 90)};
    }
    
    /**
     * Sets the target rotation
     * @param yaw Target yaw
     * @param pitch Target pitch
     */
    public static void setTargetRotation(float yaw, float pitch) {
        targetYaw = yaw;
        targetPitch = pitch;
        rotating = true;
    }
    
    /**
     * Gets the target yaw
     * @return Target yaw
     */
    public static float getTargetYaw() {
        return targetYaw;
    }
    
    /**
     * Gets the target pitch
     * @return Target pitch
     */
    public static float getTargetPitch() {
        return targetPitch;
    }
    
    /**
     * Checks if the player is rotating
     * @return Whether the player is rotating
     */
    public static boolean isRotating() {
        return rotating;
    }
    
    /**
     * Reset rotation status
     */
    public static void resetRotation() {
        rotating = false;
    }
    
    /**
     * Gets the difference between two rotations
     * @param yaw1 First yaw
     * @param pitch1 First pitch
     * @param yaw2 Second yaw
     * @param pitch2 Second pitch
     * @return Rotation difference
     */
    public static double getRotationDifference(float yaw1, float pitch1, float yaw2, float pitch2) {
        float yawDiff = MathHelper.wrapDegrees(yaw1 - yaw2);
        float pitchDiff = pitch1 - pitch2;
        
        return Math.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);
    }
    
    /**
     * Gets the rotation difference to a target
     * @param entity Target entity
     * @return Rotation difference
     */
    public static double getRotationDifference(Entity entity) {
        if (entity == null || mc.player == null) {
            return 0.0;
        }
        
        float[] rotations = getRotations(entity);
        return getRotationDifference(mc.player.rotationYaw, mc.player.rotationPitch, rotations[0], rotations[1]);
    }
    
    /**
     * Checks if the player can see a target
     * @param entity Target entity
     * @return Whether the player can see the target
     */
    public static boolean canSeeEntity(Entity entity) {
        if (entity == null || mc.player == null || mc.world == null) {
            return false;
        }
        
        Vec3d eyePos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        
        for (Vec3d lookPoint : getPointsOnEntityHitbox(entity)) {
            if (mc.world.rayTraceBlocks(eyePos, lookPoint, false, true, false) == null) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Gets points on an entity's hitbox
     * @param entity The entity
     * @return List of points
     */
    private static Vec3d[] getPointsOnEntityHitbox(Entity entity) {
        Vec3d center = entity.getPositionVector().add(0, entity.getEyeHeight() / 2, 0);
        float width = entity.width / 2.0f;
        float height = entity.height / 2.0f;
        
        return new Vec3d[] {
            center, // Center
            center.add(0, height, 0), // Top center
            center.add(0, -height, 0), // Bottom center
            center.add(width, 0, 0), // Right center
            center.add(-width, 0, 0), // Left center
            center.add(0, 0, width), // Front center
            center.add(0, 0, -width) // Back center
        };
    }
    
    /**
     * Gets a direction vector from rotations
     * @param yaw Yaw angle
     * @param pitch Pitch angle
     * @return Direction vector
     */
    public static Vec3d getVectorForRotation(float yaw, float pitch) {
        float yawCos = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float yawSin = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float pitchCos = MathHelper.cos(-pitch * 0.017453292F);
        float pitchSin = MathHelper.sin(-pitch * 0.017453292F);
        
        return new Vec3d(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }
}
