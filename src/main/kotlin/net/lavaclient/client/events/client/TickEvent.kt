/*
 * This file is part of LavaClient.
 * https://github.com/lavaclient/client
 */
package net.lavaclient.client.events.client

import net.lavaclient.client.events.Event

/**
 * Event fired each game tick
 */
class TickEvent(
    val tickCount: Int
) : Event()