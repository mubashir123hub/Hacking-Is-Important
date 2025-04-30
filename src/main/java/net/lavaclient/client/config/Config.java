package net.lavaclient.client.config;

import com.google.gson.JsonObject;

/**
 * Interface for configurations
 */
public interface Config {
    /**
     * Serializes the configuration to a JSON object
     * @return JSON object
     */
    JsonObject serialize();
    
    /**
     * Deserializes the configuration from a JSON object
     * @param json JSON object
     */
    void deserialize(JsonObject json);
}
