/*
 * This file is part of LavaClient.
 * https://github.com/lavaclient/client
 */
package net.lavaclient.client.events

/**
 * Annotation for methods that should be called when an event is posted
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subscribe