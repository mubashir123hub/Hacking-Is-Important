/*
 * This file is part of LavaClient.
 * https://github.com/lavaclient/client
 */
package net.lavaclient.client.module

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.lavaclient.client.LavaClient
import net.lavaclient.client.module.impl.client.ClickGuiModule

/**
 * Manages all modules
 */
class ModuleManager {
    // List of all modules
    private val modules = mutableListOf<Module>()
    
    /**
     * Initialize the module manager
     */
    fun init() {
        // Register modules
        registerModule(ClickGuiModule())
        
        // Log the number of registered modules
        LavaClient.INSTANCE.info("Registered ${modules.size} modules")
        
        // Register tick event for module updates
        ClientTickEvents.END_CLIENT_TICK.register {
            // Call onTick for enabled modules
            modules.filter { it.enabled }.forEach { it.onTick() }
        }
    }
    
    /**
     * Register a module
     *
     * @param module The module to register
     */
    private fun registerModule(module: Module) {
        modules.add(module)
    }
    
    /**
     * Get a module by name
     *
     * @param name The name of the module
     * @return The module or null if not found
     */
    fun getModule(name: String): Module? {
        return modules.find { it.name.equals(name, ignoreCase = true) }
    }
    
    /**
     * Get all modules
     *
     * @return All modules
     */
    fun getModules(): List<Module> {
        return modules
    }
    
    /**
     * Get modules by category
     *
     * @param category The category
     * @return Modules in the category
     */
    fun getModulesByCategory(category: Category): List<Module> {
        return modules.filter { it.category == category }
    }
}