package net.lavaclient.client.event.events;

import net.lavaclient.client.event.Event;

/**
 * Event fired on client updates (per tick)
 */
public class UpdateEvent extends Event {
    // Current stage of the update
    private final Stage stage;
    
    /**
     * Constructor
     * @param stage The update stage
     */
    public UpdateEvent(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Gets the update stage
     * @return Update stage
     */
    public Stage getStage() {
        return stage;
    }
    
    /**
     * Check if this is a pre-update event
     * @return Whether this is a pre-update event
     */
    public boolean isPre() {
        return stage == Stage.PRE;
    }
    
    /**
     * Check if this is a post-update event
     * @return Whether this is a post-update event
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
     * Update stages
     */
    public enum Stage {
        PRE,
        POST
    }
}
