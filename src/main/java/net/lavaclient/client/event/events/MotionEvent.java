package net.lavaclient.client.event.events;

import net.lavaclient.client.event.Event;

/**
 * Event fired on player motion updates
 */
public class MotionEvent extends Event {
    // Player position
    private double x, y, z;
    
    // Player rotation
    private float yaw, pitch;
    
    // Player state
    private boolean onGround;
    
    // Event state
    private final Stage stage;
    
    /**
     * Constructor
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param yaw Yaw rotation
     * @param pitch Pitch rotation
     * @param onGround Whether the player is on ground
     * @param stage Event stage
     */
    public MotionEvent(double x, double y, double z, float yaw, float pitch, boolean onGround, Stage stage) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.stage = stage;
    }
    
    /**
     * Gets the X position
     * @return X position
     */
    public double getX() {
        return x;
    }
    
    /**
     * Sets the X position
     * @param x New X position
     */
    public void setX(double x) {
        this.x = x;
    }
    
    /**
     * Gets the Y position
     * @return Y position
     */
    public double getY() {
        return y;
    }
    
    /**
     * Sets the Y position
     * @param y New Y position
     */
    public void setY(double y) {
        this.y = y;
    }
    
    /**
     * Gets the Z position
     * @return Z position
     */
    public double getZ() {
        return z;
    }
    
    /**
     * Sets the Z position
     * @param z New Z position
     */
    public void setZ(double z) {
        this.z = z;
    }
    
    /**
     * Gets the yaw rotation
     * @return Yaw rotation
     */
    public float getYaw() {
        return yaw;
    }
    
    /**
     * Sets the yaw rotation
     * @param yaw New yaw rotation
     */
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
    
    /**
     * Gets the pitch rotation
     * @return Pitch rotation
     */
    public float getPitch() {
        return pitch;
    }
    
    /**
     * Sets the pitch rotation
     * @param pitch New pitch rotation
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
    
    /**
     * Gets whether the player is on ground
     * @return Whether the player is on ground
     */
    public boolean isOnGround() {
        return onGround;
    }
    
    /**
     * Sets whether the player is on ground
     * @param onGround New on ground state
     */
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
    
    /**
     * Gets the event stage
     * @return Event stage
     */
    public Stage getStage() {
        return stage;
    }
    
    /**
     * Check if this is a pre-motion event
     * @return Whether this is a pre-motion event
     */
    public boolean isPre() {
        return stage == Stage.PRE;
    }
    
    /**
     * Check if this is a post-motion event
     * @return Whether this is a post-motion event
     */
    public boolean isPost() {
        return stage == Stage.POST;
    }
    
    /**
     * Gets the event type based on stage
     * @return Event type
     */
    @Override
    public Type getType() {
        return stage == Stage.PRE ? Type.PRE : Type.POST;
    }
    
    /**
     * Motion event stages
     */
    public enum Stage {
        PRE,
        POST
    }
}
