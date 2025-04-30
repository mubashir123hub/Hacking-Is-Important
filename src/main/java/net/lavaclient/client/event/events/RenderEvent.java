package net.lavaclient.client.event.events;

import net.lavaclient.client.event.Event;
import net.minecraft.client.renderer.RenderGlobal;

/**
 * Event fired during rendering
 */
public class RenderEvent extends Event {
    // Render pass
    private final Pass pass;
    
    // Render context
    private final RenderGlobal context;
    
    // Partial ticks
    private final float partialTicks;
    
    /**
     * Constructor
     * @param pass Render pass
     * @param context Render context
     * @param partialTicks Partial ticks
     */
    public RenderEvent(Pass pass, RenderGlobal context, float partialTicks) {
        this.pass = pass;
        this.context = context;
        this.partialTicks = partialTicks;
    }
    
    /**
     * Gets the render pass
     * @return Render pass
     */
    public Pass getPass() {
        return pass;
    }
    
    /**
     * Gets the render context
     * @return Render context
     */
    public RenderGlobal getContext() {
        return context;
    }
    
    /**
     * Gets the partial ticks
     * @return Partial ticks
     */
    public float getPartialTicks() {
        return partialTicks;
    }
    
    /**
     * Check if this is a pre-render event
     * @return Whether this is a pre-render event
     */
    public boolean isPre() {
        return pass == Pass.PRE;
    }
    
    /**
     * Check if this is a post-render event
     * @return Whether this is a post-render event
     */
    public boolean isPost() {
        return pass == Pass.POST;
    }
    
    /**
     * Gets the event type based on pass
     * @return Event type
     */
    @Override
    public Type getType() {
        return pass == Pass.PRE ? Type.PRE : Type.POST;
    }
    
    /**
     * Render passes
     */
    public enum Pass {
        PRE,
        POST
    }
}
