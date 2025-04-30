/*
 * This file is part of LavaClient.
 * https://github.com/lavaclient/client
 */
package net.lavaclient.client

import net.fabricmc.api.ClientModInitializer
import net.lavaclient.client.events.EventManager
import net.lavaclient.client.module.ModuleManager
import net.lavaclient.client.util.KeyBindManager
import org.slf4j.LoggerFactory

/**
 * Main class for the client
 */
class LavaClient : ClientModInitializer {
    // Logger for the client
    private val logger = LoggerFactory.getLogger("LavaClient")
    
    // Event manager
    lateinit var eventManager: EventManager
        private set
    
    // Module manager
    lateinit var moduleManager: ModuleManager
        private set
    
    // Key bind manager
    lateinit var keyBindManager: KeyBindManager
        private set
    
    override fun onInitializeClient() {
        INSTANCE = this
        
        info("Initializing LavaClient")
        
        // Initialize event manager
        eventManager = EventManager()
        
        // Initialize module manager
        moduleManager = ModuleManager()
        moduleManager.init()
        
        // Initialize key bind manager
        keyBindManager = KeyBindManager()
        keyBindManager.init()
        
        info("LavaClient initialized")
    }
    
    /**
     * Log info message
     *
     * @param message The message
     */
    fun info(message: String) {
        logger.info(message)
    }
    
    /**
     * Log debug message
     *
     * @param message The message
     */
    fun debug(message: String) {
        logger.debug(message)
    }
    
    /**
     * Log warn message
     *
     * @param message The message
     */
    fun warn(message: String) {
        logger.warn(message)
    }
    
    /**
     * Log error message
     *
     * @param message The message
     */
    fun error(message: String) {
        logger.error(message)
    }
    
    companion object {
        /**
         * Instance of the client
         */
        lateinit var INSTANCE: LavaClient
            private set
    }
}