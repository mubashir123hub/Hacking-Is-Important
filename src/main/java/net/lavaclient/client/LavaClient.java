package net.lavaclient.client;

import net.lavaclient.client.account.AccountManager;
import net.lavaclient.client.config.ConfigManager;
import net.lavaclient.client.event.EventManager;
import net.lavaclient.client.module.ModuleManager;
import net.lavaclient.client.ui.clickgui.ClickGUI;
import net.lavaclient.client.ui.hud.HUDManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;

import java.io.File;

/**
 * Main class for Lava Client
 * Enhanced LiquidBounce fork with improved modules, visuals and performance
 */
public class LavaClient {
    // Singleton instance
    private static LavaClient instance;
    
    // Client information
    public static final String NAME = "Lava Client";
    public static final String VERSION = "1.0.0";
    public static final String AUTHOR = "Lava Development";
    
    // Client managers
    private ModuleManager moduleManager;
    private EventManager eventManager;
    private HUDManager hudManager;
    private ConfigManager configManager;
    private AccountManager accountManager;
    
    // Client UI
    private ClickGUI clickGUI;
    
    // Client directory
    private File clientDir;
    
    /**
     * Constructor initializes the client
     */
    public LavaClient() {
        instance = this;
    }
    
    /**
     * Starts the client
     */
    public void startClient() {
        // Set window title
        Display.setTitle(NAME + " " + VERSION);
        
        // Initialize client directory
        clientDir = new File(Minecraft.getMinecraft().mcDataDir, NAME);
        if (!clientDir.exists()) {
            clientDir.mkdir();
        }
        
        // Initialize managers
        eventManager = new EventManager();
        moduleManager = new ModuleManager();
        hudManager = new HUDManager();
        configManager = new ConfigManager();
        accountManager = new AccountManager();
        
        // Initialize UI
        clickGUI = new ClickGUI();
        
        // Load configurations
        configManager.loadConfigs();
        
        System.out.println(NAME + " " + VERSION + " initialized successfully!");
    }
    
    /**
     * Stops the client
     */
    public void stopClient() {
        // Save configurations
        configManager.saveConfigs();
        
        System.out.println(NAME + " " + VERSION + " stopped!");
    }
    
    /**
     * Gets the singleton instance
     * @return The client instance
     */
    public static LavaClient getInstance() {
        return instance;
    }
    
    /**
     * Gets the module manager
     * @return The module manager
     */
    public ModuleManager getModuleManager() {
        return moduleManager;
    }
    
    /**
     * Gets the event manager
     * @return The event manager
     */
    public EventManager getEventManager() {
        return eventManager;
    }
    
    /**
     * Gets the HUD manager
     * @return The HUD manager
     */
    public HUDManager getHudManager() {
        return hudManager;
    }
    
    /**
     * Gets the config manager
     * @return The config manager
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    /**
     * Gets the account manager
     * @return The account manager
     */
    public AccountManager getAccountManager() {
        return accountManager;
    }
    
    /**
     * Gets the click GUI
     * @return The click GUI
     */
    public ClickGUI getClickGUI() {
        return clickGUI;
    }
    
    /**
     * Gets the client directory
     * @return The client directory
     */
    public File getClientDir() {
        return clientDir;
    }
}
