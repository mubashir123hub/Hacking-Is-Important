package net.lavaclient.client.event;

/**
 * Base class for events
 */
public abstract class Event {
    /**
     * Whether the event has been cancelled
     */
    private boolean cancelled;
    
    /**
     * Constructor
     */
    public Event() {
        this.cancelled = false;
    }
    
    /**
     * Gets whether the event has been cancelled
     * @return Whether the event has been cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }
    
    /**
     * Sets whether the event should be cancelled
     * @param cancelled New cancelled state
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    /**
     * Event types for event priority
     */
    public enum Type {
        PRE,
        POST
    }
    
    /**
     * Gets the event type
     * @return Event type
     */
    public Type getType() {
        return Type.PRE;
    }
    
    /**
     * Checks if the event is of a specific type
     * @param type Type to check
     * @return Whether the event is of the type
     */
    public boolean isPre() {
        return getType() == Type.PRE;
    }
    
    /**
     * Checks if the event is post
     * @return Whether the event is post
     */
    public boolean isPost() {
        return getType() == Type.POST;
    }
}
