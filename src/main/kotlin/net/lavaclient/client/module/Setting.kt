/*
 * This file is part of LavaClient.
 * https://github.com/lavaclient/client
 */
package net.lavaclient.client.module

/**
 * A module setting
 */
abstract class Setting<T>(
    val name: String,
    val description: String,
    var value: T
) {
    // Listeners
    private val changeListeners = mutableListOf<(T) -> Unit>()
    
    /**
     * Set the value of the setting
     *
     * @param value The new value
     */
    open fun setValue(value: T) {
        val oldValue = this.value
        this.value = value
        
        // Call change listeners
        if (oldValue != value) {
            changeListeners.forEach { it(value) }
        }
    }
    
    /**
     * Add a change listener
     *
     * @param listener The listener
     */
    fun addChangeListener(listener: (T) -> Unit) {
        changeListeners.add(listener)
    }
    
    /**
     * Remove a change listener
     *
     * @param listener The listener
     */
    fun removeChangeListener(listener: (T) -> Unit) {
        changeListeners.remove(listener)
    }
    
    /**
     * Clear all change listeners
     */
    fun clearChangeListeners() {
        changeListeners.clear()
    }
}