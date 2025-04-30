/*
 * This file is part of LavaClient.
 * https://github.com/lavaclient/client
 */
package net.lavaclient.client.events

/**
 * Interface for events that can be cancelled
 */
interface Cancellable {
    /**
     * Check if the event is cancelled
     *
     * @return true if the event is cancelled
     */
    fun isCancelled(): Boolean
    
    /**
     * Set the cancelled state of the event
     *
     * @param cancelled Whether the event should be cancelled
     */
    fun setCancelled(cancelled: Boolean)
}