package net.lavaclient.client.module;

import net.lavaclient.client.LavaClient;
import net.lavaclient.client.event.EventTarget;
import net.lavaclient.client.event.events.RenderEvent;
import net.lavaclient.client.value.Value;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

/**
 * Base module class for all client modules
 */
public abstract class Module {
    // Minecraft instance for convenience
    protected final Minecraft mc = Minecraft.getMinecraft();
    
    // Module information
    private final String name;
    private final String description;
    private final Category category;
    
    // Module state
    private boolean state = false;
    private int keyBind;
    
    // Module values/settings
    private final List<Value<?>> values = new ArrayList<>();
    
    // Animation for smooth rendering
    private float slideAnimation = 0f;
    
    /**
     * Constructor
     * @param name Module name
     * @param description Module description
     * @param category Module category
     */
    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }
    
    /**
     * Called when the module is enabled
     */
    public void onEnable() {
        // Register to event manager when enabled
        LavaClient.getInstance().getEventManager().register(this);
    }
    
    /**
     * Called when the module is disabled
     */
    public void onDisable() {
        // Unregister from event manager when disabled
        LavaClient.getInstance().getEventManager().unregister(this);
    }
    
    /**
     * Toggles the module state
     */
    public void toggle() {
        setState(!state);
    }
    
    /**
     * Sets the module state
     * @param state New state
     */
    public void setState(boolean state) {
        if (this.state == state) return;
        
        this.state = state;
        
        if (state) {
            onEnable();
        } else {
            onDisable();
        }
    }
    
    /**
     * Render event target
     * @param event Render event
     */
    @EventTarget
    public void onRender(RenderEvent event) {
        // Update slide animation for smooth rendering in HUD
        if (state) {
            if (slideAnimation < 1) {
                slideAnimation = Math.min(1, slideAnimation + 0.1f);
            }
        } else {
            if (slideAnimation > 0) {
                slideAnimation = Math.max(0, slideAnimation - 0.1f);
            }
        }
    }
    
    /**
     * Adds a value/setting to the module
     * @param value The value to add
     * @param <T> The value type
     * @return The added value for chaining
     */
    protected <T> Value<T> addValue(Value<T> value) {
        values.add(value);
        return value;
    }
    
    /**
     * Gets the module name
     * @return Module name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the module description
     * @return Module description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Gets the module category
     * @return Module category
     */
    public Category getCategory() {
        return category;
    }
    
    /**
     * Gets the module state
     * @return Module state
     */
    public boolean getState() {
        return state;
    }
    
    /**
     * Gets the module key bind
     * @return Module key bind
     */
    public int getKeyBind() {
        return keyBind;
    }
    
    /**
     * Sets the module key bind
     * @param keyBind New key bind
     */
    public void setKeyBind(int keyBind) {
        this.keyBind = keyBind;
    }
    
    /**
     * Gets the list of module values/settings
     * @return List of values
     */
    public List<Value<?>> getValues() {
        return values;
    }
    
    /**
     * Gets the slide animation value
     * @return Slide animation value
     */
    public float getSlideAnimation() {
        return slideAnimation;
    }
    
    /**
     * Module categories
     */
    public enum Category {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        RENDER("Render"),
        PLAYER("Player"),
        WORLD("World"),
        MISC("Misc");
        
        private final String displayName;
        
        Category(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
