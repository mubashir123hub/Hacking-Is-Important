/*
 * This file is part of LavaClient.
 * https://github.com/lavaclient/client
 */
package net.lavaclient.client.events

/**
 * Base class for all events
 */
open class Event {
    private var cancelled = false
    
    /**
     * Cancel the event
     */
    fun cancel() {
        cancelled = true
    }
    
    /**
     * Check if the event is cancelled
     *
     * @return True if cancelled, false otherwise
     */
    fun isCancelled(): Boolean {
        return cancelled
    }
}