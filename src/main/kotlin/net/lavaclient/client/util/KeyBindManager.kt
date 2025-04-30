/*
 * This file is part of LavaClient.
 * https://github.com/lavaclient/client
 */
package net.lavaclient.client.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.lavaclient.client.LavaClient
import net.lavaclient.client.events.client.KeyEvent
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.Window
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import org.lwjgl.glfw.GLFW

/**
 * Manages all keybindings for the client
 */
class KeyBindManager {
    // Map of key codes to registered actions
    private val keyCallbacks = mutableMapOf<Int, MutableList<() -> Unit>>()

    // Flag to prevent multiple GUI opens
    private var rightShiftWasPressed = false

    /**
     * Initialize the key binding manager
     */
    fun init() {
        // Register client tick event for key handling
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            // Get window handle safely
            // Check for ClickGUI key (right shift)
            val rightShiftPressed = InputUtil.isKeyPressed(client.window.handle, GLFW.GLFW_KEY_RIGHT_SHIFT)

            // Open GUI on key press (but not on key hold)
            if (rightShiftPressed && !rightShiftWasPressed) {
                // Toggle clickgui module to open the GUI
                val clickGuiModule = LavaClient.INSTANCE.moduleManager.getModule("ClickGUI")
                clickGuiModule?.toggle()
            }

            // Update key state
            rightShiftWasPressed = rightShiftPressed

            // Check for other key presses
            keyCallbacks.forEach { (keyCode, actions) ->
                if (InputUtil.isKeyPressed(client.window.handle, keyCode)) {
                    val keyEvent = KeyEvent(keyCode)
                    LavaClient.INSTANCE.eventManager.post(keyEvent)

                    if (!keyEvent.isCancelled()) {
                        actions.forEach { it.invoke() }
                    }
                }
            }
        }
    }

    /**
     * Register a key callback
     *
     * @param keyCode The key code
     * @param callback The callback to run when the key is pressed
     */
    fun registerKeyCallback(keyCode: Int, callback: () -> Unit) {
        if (!keyCallbacks.containsKey(keyCode)) {
            keyCallbacks[keyCode] = mutableListOf()
        }

        keyCallbacks[keyCode]?.add(callback)
    }

    /**
     * Unregister a key callback
     *
     * @param keyCode The key code
     * @param callback The callback to remove
     */
    fun unregisterKeyCallback(keyCode: Int, callback: () -> Unit) {
        keyCallbacks[keyCode]?.remove(callback)

        if (keyCallbacks[keyCode]?.isEmpty() == true) {
            keyCallbacks.remove(keyCode)
        }
    }

    /**
     * Get the key name for a key code
     *
     * @param keyCode The key code
     * @return The key name
     */
    fun getKeyName(keyCode: Int): String {
        return when (keyCode) {
            -1 -> "None"
            GLFW.GLFW_KEY_RIGHT_SHIFT -> "Right Shift"
            GLFW.GLFW_KEY_LEFT_SHIFT -> "Left Shift"
            GLFW.GLFW_KEY_RIGHT_CONTROL -> "Right Ctrl"
            GLFW.GLFW_KEY_LEFT_CONTROL -> "Left Ctrl"
            GLFW.GLFW_KEY_RIGHT_ALT -> "Right Alt"
            GLFW.GLFW_KEY_LEFT_ALT -> "Left Alt"
            GLFW.GLFW_KEY_SPACE -> "Space"
            GLFW.GLFW_KEY_TAB -> "Tab"
            GLFW.GLFW_KEY_CAPS_LOCK -> "Caps Lock"
            GLFW.GLFW_KEY_ESCAPE -> "Escape"
            in GLFW.GLFW_KEY_A..GLFW.GLFW_KEY_Z -> ('A' + (keyCode - GLFW.GLFW_KEY_A)).toString()
            in GLFW.GLFW_KEY_0..GLFW.GLFW_KEY_9 -> (keyCode - GLFW.GLFW_KEY_0).toString()
            in GLFW.GLFW_KEY_F1..GLFW.GLFW_KEY_F12 -> "F${keyCode - GLFW.GLFW_KEY_F1 + 1}"
            else -> "Key $keyCode"
        }
    }
}
