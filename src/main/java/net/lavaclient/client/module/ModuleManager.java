package net.lavaclient.client.module;

import net.lavaclient.client.event.EventTarget;
import net.lavaclient.client.event.events.UpdateEvent;
import net.lavaclient.client.module.modules.combat.Criticals;
import net.lavaclient.client.module.modules.combat.KillAura;
import net.lavaclient.client.module.modules.combat.Velocity;
import net.lavaclient.client.module.modules.misc.AntiBot;
import net.lavaclient.client.module.modules.movement.Flight;
import net.lavaclient.client.module.modules.movement.Speed;
import net.lavaclient.client.module.modules.player.NoFall;
import net.lavaclient.client.module.modules.render.HUD;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages all client modules
 */
public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();
    
    /**
     * Constructor initializes all modules
     */
    public ModuleManager() {
        // Register all modules
        
        // Combat
        registerModule(new KillAura());
        registerModule(new Velocity());
        registerModule(new Criticals());
        registerModule(new AntiBot());
        
        // Movement
        registerModule(new Speed());
        registerModule(new Flight());
        
        // Player
        registerModule(new NoFall());
        
        // Render
        registerModule(new HUD());
        
        // Set default keybinds
        getModule(KillAura.class).setKeyBind(Keyboard.KEY_R);
        getModule(Velocity.class).setKeyBind(Keyboard.KEY_V);
        getModule(Speed.class).setKeyBind(Keyboard.KEY_X);
        getModule(Flight.class).setKeyBind(Keyboard.KEY_F);
        getModule(NoFall.class).setKeyBind(Keyboard.KEY_N);
        
        // HUD is always enabled
        getModule(HUD.class).setState(true);
        
        System.out.println("Registered " + modules.size() + " modules");
    }
    
    /**
     * Registers a module
     * @param module The module to register
     */
    public void registerModule(Module module) {
        modules.add(module);
    }
    
    /**
     * Gets a module by class
     * @param clazz The module class
     * @param <T> The module type
     * @return The module instance
     */
    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module module : modules) {
            if (module.getClass() == clazz) {
                return (T) module;
            }
        }
        
        return null;
    }
    
    /**
     * Gets a module by name
     * @param name The module name
     * @return The module instance
     */
    public Module getModule(String name) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        
        return null;
    }
    
    /**
     * Gets all modules
     * @return List of all modules
     */
    public List<Module> getModules() {
        return modules;
    }
    
    /**
     * Gets modules by category
     * @param category The category
     * @return List of modules in the category
     */
    public List<Module> getModulesByCategory(Module.Category category) {
        return modules.stream()
                .filter(module -> module.getCategory() == category)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets enabled modules
     * @return List of enabled modules
     */
    public List<Module> getEnabledModules() {
        return modules.stream()
                .filter(Module::getState)
                .collect(Collectors.toList());
    }
    
    /**
     * Update event target for key binds
     * @param event Update event
     */
    @EventTarget
    public void onUpdate(UpdateEvent event) {
        // Check key binds
        for (Module module : modules) {
            if (module.getKeyBind() != 0 && Keyboard.isKeyDown(module.getKeyBind())) {
                module.toggle();
            }
        }
    }
}
