package net.lavaclient.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.lavaclient.client.LavaClient;
import net.lavaclient.client.module.Module;
import net.lavaclient.client.value.*;

import java.io.*;
import java.util.Map;

/**
 * Manager for client configurations
 */
public class ConfigManager {
    // Config files
    private final File configDir;
    private final File modulesConfig;
    private final File clientConfig;
    
    // Gson for serialization
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * Constructor
     */
    public ConfigManager() {
        // Initialize the config directory
        configDir = new File(LavaClient.getInstance().getClientDir(), "configs");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        
        // Initialize config files
        modulesConfig = new File(configDir, "modules.json");
        clientConfig = new File(configDir, "client.json");
    }
    
    /**
     * Loads all configurations
     */
    public void loadConfigs() {
        // Load the modules configuration
        loadModules();
        
        // Load the client configuration
        loadClient();
        
        System.out.println("Configurations loaded successfully");
    }
    
    /**
     * Saves all configurations
     */
    public void saveConfigs() {
        // Save the modules configuration
        saveModules();
        
        // Save the client configuration
        saveClient();
        
        System.out.println("Configurations saved successfully");
    }
    
    /**
     * Loads module configurations
     */
    private void loadModules() {
        // If the file doesn't exist, return
        if (!modulesConfig.exists()) {
            return;
        }
        
        try (Reader reader = new FileReader(modulesConfig)) {
            // Parse the JSON
            JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
            
            // Get the module manager
            LavaClient client = LavaClient.getInstance();
            
            // Iterate through each module
            for (Module module : client.getModuleManager().getModules()) {
                // Get the module name
                String name = module.getName();
                
                // If the module isn't in the config, skip it
                if (!json.has(name)) {
                    continue;
                }
                
                // Get the module's JSON object
                JsonObject moduleJson = json.getAsJsonObject(name);
                
                // Load the enabled state
                if (moduleJson.has("enabled")) {
                    boolean enabled = moduleJson.get("enabled").getAsBoolean();
                    module.setState(enabled);
                }
                
                // Load the keybind
                if (moduleJson.has("keyBind")) {
                    int keyBind = moduleJson.get("keyBind").getAsInt();
                    module.setKeyBind(keyBind);
                }
                
                // Load the values
                if (moduleJson.has("values")) {
                    JsonObject valuesJson = moduleJson.getAsJsonObject("values");
                    
                    // Iterate through each value
                    for (Value<?> value : module.getValues()) {
                        // Get the value name
                        String valueName = value.getName();
                        
                        // If the value isn't in the config, skip it
                        if (!valuesJson.has(valueName)) {
                            continue;
                        }
                        
                        // Load the value based on its type
                        if (value instanceof BoolValue) {
                            BoolValue boolValue = (BoolValue) value;
                            boolValue.fromString(valuesJson.get(valueName).getAsString());
                        } else if (value instanceof FloatValue) {
                            FloatValue floatValue = (FloatValue) value;
                            floatValue.fromString(valuesJson.get(valueName).getAsString());
                        } else if (value instanceof IntegerValue) {
                            IntegerValue integerValue = (IntegerValue) value;
                            integerValue.fromString(valuesJson.get(valueName).getAsString());
                        } else if (value instanceof ListValue) {
                            ListValue listValue = (ListValue) value;
                            listValue.fromString(valuesJson.get(valueName).getAsString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading modules config: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Saves module configurations
     */
    private void saveModules() {
        try (Writer writer = new FileWriter(modulesConfig)) {
            // Create the root JSON object
            JsonObject json = new JsonObject();
            
            // Get the module manager
            LavaClient client = LavaClient.getInstance();
            
            // Iterate through each module
            for (Module module : client.getModuleManager().getModules()) {
                // Create a JSON object for the module
                JsonObject moduleJson = new JsonObject();
                
                // Save the enabled state
                moduleJson.addProperty("enabled", module.getState());
                
                // Save the keybind
                moduleJson.addProperty("keyBind", module.getKeyBind());
                
                // Save the values
                JsonObject valuesJson = new JsonObject();
                
                // Iterate through each value
                for (Value<?> value : module.getValues()) {
                    // Save the value as a string
                    valuesJson.addProperty(value.getName(), value.toString());
                }
                
                // Add the values to the module
                moduleJson.add("values", valuesJson);
                
                // Add the module to the root
                json.add(module.getName(), moduleJson);
            }
            
            // Write the JSON to the file
            gson.toJson(json, writer);
        } catch (Exception e) {
            System.err.println("Error saving modules config: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads client configuration
     */
    private void loadClient() {
        // If the file doesn't exist, return
        if (!clientConfig.exists()) {
            return;
        }
        
        try (Reader reader = new FileReader(clientConfig)) {
            // Parse the JSON
            JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
            
            // Load client settings
            // In a real implementation, this would load global client settings
            // For this example, we'll just log that it was loaded
            System.out.println("Loaded client configuration");
        } catch (Exception e) {
            System.err.println("Error loading client config: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Saves client configuration
     */
    private void saveClient() {
        try (Writer writer = new FileWriter(clientConfig)) {
            // Create the root JSON object
            JsonObject json = new JsonObject();
            
            // Save client settings
            // In a real implementation, this would save global client settings
            // For this example, we'll just save a placeholder
            json.addProperty("version", LavaClient.VERSION);
            
            // Write the JSON to the file
            gson.toJson(json, writer);
        } catch (Exception e) {
            System.err.println("Error saving client config: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Saves a specific configuration
     * @param name Config name
     * @param config Config object
     */
    public void saveConfig(String name, Config config) {
        try {
            // Create the config file
            File file = new File(configDir, name + ".json");
            
            // Write the config to the file
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(config.serialize(), writer);
            }
        } catch (Exception e) {
            System.err.println("Error saving config: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads a specific configuration
     * @param name Config name
     * @param config Config object
     */
    public void loadConfig(String name, Config config) {
        try {
            // Get the config file
            File file = new File(configDir, name + ".json");
            
            // If the file doesn't exist, return
            if (!file.exists()) {
                return;
            }
            
            // Read the config from the file
            try (Reader reader = new FileReader(file)) {
                JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
                config.deserialize(json);
            }
        } catch (Exception e) {
            System.err.println("Error loading config: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Lists available configurations
     * @return Array of config names
     */
    public String[] listConfigs() {
        // Get all JSON files in the config directory
        File[] files = configDir.listFiles((dir, name) -> name.endsWith(".json") && !name.equals("modules.json") && !name.equals("client.json"));
        
        // If no files, return an empty array
        if (files == null) {
            return new String[0];
        }
        
        // Create an array of config names
        String[] configs = new String[files.length];
        
        // Fill the array
        for (int i = 0; i < files.length; i++) {
            configs[i] = files[i].getName().substring(0, files[i].getName().length() - 5);
        }
        
        return configs;
    }
    
    /**
     * Deletes a configuration
     * @param name Config name
     * @return Whether the deletion was successful
     */
    public boolean deleteConfig(String name) {
        // Get the config file
        File file = new File(configDir, name + ".json");
        
        // If the file doesn't exist, return false
        if (!file.exists()) {
            return false;
        }
        
        // Delete the file
        return file.delete();
    }
}
