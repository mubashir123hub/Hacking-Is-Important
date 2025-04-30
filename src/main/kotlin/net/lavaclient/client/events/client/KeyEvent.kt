/*
 * This file is part of LavaClient.
 * https://github.com/lavaclient/client
 */
package net.lavaclient.client.events.client

import net.lavaclient.client.events.Cancellable

/**
 * Event fired when a key is pressed
 */
class KeyEvent(
    /**
     * The key code
     */
    val keyCode: Int
) : Cancellable {
    private var cancelled = false
    
    override fun isCancelled(): Boolean {
        return cancelled
    }
    
    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }
}