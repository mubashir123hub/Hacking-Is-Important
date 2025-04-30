package net.lavaclient.client.value;

/**
 * Boolean value class for module settings
 */
public class BoolValue extends Value<Boolean> {
    /**
     * Constructor with name only
     * @param name Value name
     * @param value Default value
     */
    public BoolValue(String name, Boolean value) {
        super(name, value);
    }
    
    /**
     * Constructor with name and description
     * @param name Value name
     * @param description Value description
     * @param value Default value
     */
    public BoolValue(String name, String description, Boolean value) {
        super(name, description, value);
    }
    
    /**
     * Toggles the value
     * @return New value
     */
    public boolean toggle() {
        set(!get());
        return get();
    }
    
    /**
     * Parses a string to a boolean value
     * @param str String to parse
     * @return Parsed value or false if invalid
     */
    public boolean fromString(String str) {
        try {
            set(Boolean.parseBoolean(str));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the value as a string
     * @return String representation
     */
    @Override
    public String toString() {
        return get().toString();
    }
}
