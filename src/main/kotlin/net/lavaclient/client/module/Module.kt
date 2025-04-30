/*
 * This file is part of LavaClient.
 * https://github.com/lavaclient/client
 */
package net.lavaclient.client.module

import net.lavaclient.client.LavaClient
import org.lwjgl.glfw.GLFW

/**
 * Base class for all modules
 */
open class Module(val name: String, val category: Category) {
    // Is the module enabled
    var enabled = false
        private set
    
    // Key code for the module
    var keyCode = GLFW.GLFW_KEY_UNKNOWN
    
    // Description of the module
    var description = "No description provided"
    
    /**
     * Toggle the module on or off
     */
    fun toggle() {
        enabled = !enabled
        
        if (enabled) {
            onEnable()
            LavaClient.INSTANCE.info("Module $name enabled")
        } else {
            onDisable()
            LavaClient.INSTANCE.info("Module $name disabled")
        }
    }
    
    /**
     * Called when the module is enabled
     */
    protected open fun onEnable() {}
    
    /**
     * Called when the module is disabled
     */
    protected open fun onDisable() {}
    
    /**
     * Called every tick when the module is enabled
     */
    open fun onTick() {}
    
    /**
     * Enable the module
     */
    fun enable() {
        if (!enabled) {
            toggle()
        }
    }
    
    /**
     * Disable the module
     */
    fun disable() {
        if (enabled) {
            toggle()
        }
    }
}