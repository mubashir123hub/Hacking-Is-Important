package net.lavaclient.client;

import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

/**
 * Client loader for Forge
 */
@Mod(modid = "lavaclient", name = "Lava Client", version = "1.0.0", acceptedMinecraftVersions = "[1.12.2]")
public class ClientLoader {
    
    // Client instance
    private LavaClient lavaClient;
    
    /**
     * Pre-initialization event
     * @param event The pre-initialization event
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("Lava Client pre-initialization...");
    }
    
    /**
     * Initialization event
     * @param event The initialization event
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("Lava Client initialization...");
        
        // Initialize client
        lavaClient = new LavaClient();
        lavaClient.startClient();
    }
    
    /**
     * Post-initialization event
     * @param event The post-initialization event
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        System.out.println("Lava Client post-initialization...");
    }
    
    /**
     * Key input event
     * @param event The key input event
     */
    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        // Check if right shift is pressed to toggle click GUI
        if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            Minecraft.getMinecraft().displayGuiScreen(lavaClient.getClickGUI());
        }
    }
}
