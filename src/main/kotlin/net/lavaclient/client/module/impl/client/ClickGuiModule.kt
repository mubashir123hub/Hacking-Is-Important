/*
 * This file is part of LavaClient.
 * https://github.com/lavaclient/client
 */
package net.lavaclient.client.module.impl.client

import net.lavaclient.client.module.Category
import net.lavaclient.client.module.Module
import org.lwjgl.glfw.GLFW

/**
 * Module that opens the click GUI
 */
class ClickGuiModule : Module("ClickGUI", Category.CLIENT) {
    init {
        description = "Opens the click GUI"
        keyCode = GLFW.GLFW_KEY_RIGHT_SHIFT
    }
    
    override fun onEnable() {
        // Since the GUI is opened on toggle, we can disable the module right away
        disable()
    }
}