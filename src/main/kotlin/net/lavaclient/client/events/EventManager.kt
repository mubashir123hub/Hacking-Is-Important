/*
 * This file is part of LavaClient.
 * https://github.com/lavaclient/client
 */
package net.lavaclient.client.events

import net.lavaclient.client.LavaClient
import java.lang.reflect.Method

/**
 * Manages event subscriptions and posts events
 */
class EventManager {
    // Map of event classes to listeners
    private val listeners = mutableMapOf<Class<*>, MutableList<Pair<Any, Method>>>()
    
    /**
     * Register an object to receive events
     *
     * @param obj The object to register
     */
    fun register(obj: Any) {
        // Find all methods with Subscribe annotation
        for (method in obj.javaClass.declaredMethods) {
            if (method.isAnnotationPresent(Subscribe::class.java)) {
                // Method must have exactly one parameter
                if (method.parameterCount != 1) {
                    LavaClient.INSTANCE.warn("Event handler method ${method.name} in ${obj.javaClass.name} has invalid parameter count (expected 1, got ${method.parameterCount})")
                    continue
                }
                
                // Get the event class from the parameter
                val eventClass = method.parameterTypes[0]
                
                // Register the listener
                if (!listeners.containsKey(eventClass)) {
                    listeners[eventClass] = mutableListOf()
                }
                
                // Make the method accessible (in case it's private)
                method.isAccessible = true
                
                // Add the listener to the list
                listeners[eventClass]?.add(Pair(obj, method))
                
                LavaClient.INSTANCE.debug("Registered event handler ${method.name} in ${obj.javaClass.name} for event ${eventClass.name}")
            }
        }
    }
    
    /**
     * Unregister an object from receiving events
     *
     * @param obj The object to unregister
     */
    fun unregister(obj: Any) {
        // Remove all listeners for the object
        listeners.forEach { (_, listeners) ->
            listeners.removeIf { (listener, _) -> listener == obj }
        }
        
        // Remove empty lists
        listeners.entries.removeIf { (_, listeners) -> listeners.isEmpty() }
    }
    
    /**
     * Post an event to all listeners
     *
     * @param event The event to post
     * @return The event
     */
    fun <T> post(event: T): T {
        // Get the event class
        val eventClass = event!!.javaClass
        
        // Get all listeners for the event and its parent classes
        val eventListeners = mutableListOf<Pair<Any, Method>>()
        
        listeners.forEach { (clazz, listeners) ->
            if (clazz.isAssignableFrom(eventClass)) {
                eventListeners.addAll(listeners)
            }
        }
        
        // Call all listeners
        eventListeners.forEach { (obj, method) ->
            try {
                method.invoke(obj, event)
            } catch (e: Exception) {
                LavaClient.INSTANCE.error("Error calling event handler ${method.name} in ${obj.javaClass.name}")
                e.printStackTrace()
            }
            
            // Break if the event is cancelled
            if (event is Cancellable && event.isCancelled()) {
                return event
            }
        }
        
        return event
    }
}