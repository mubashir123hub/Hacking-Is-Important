package net.lavaclient.client.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents // Fixed duplicate imports
import net.lavaclient.client.LavaClient
import org.lwjgl.glfw.GLFW

/**
 * Manages all keybindings for the client.
 */
class KeyBindManager {
    private val keyCallbacks = mutableMapOf<Int, MutableList<() -> Unit>>()
    private var rightShiftWasPressed = false

    fun init() {
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            val rightShiftPressed = GLFW.glfwGetKey(client.window.handle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS
            if (rightShiftPressed && !rightShiftWasPressed) {
                LavaClient.INSTANCE.moduleManager.getModule("ClickGUI")?.toggle()
            }
            rightShiftWasPressed = rightShiftPressed
        }
    }

    fun registerKeyCallback(keyCode: Int, callback: () -> Unit) {
        keyCallbacks.computeIfAbsent(keyCode) { mutableListOf() }.add(callback)
    }

    fun unregisterKeyCallback(keyCode: Int, callback: () -> Unit) {
        keyCallbacks[keyCode]?.remove(callback)
        if (keyCallbacks[keyCode]?.isEmpty() == true) {
            keyCallbacks.remove(keyCode)
        }
    }
}
