package net.lavaclient.client.value;

/**
 * Integer value class for module settings
 */
public class IntegerValue extends Value<Integer> {
    private final int minimum;
    private final int maximum;
    
    /**
     * Constructor
     * @param name Value name
     * @param value Default value
     * @param minimum Minimum value
     * @param maximum Maximum value
     */
    public IntegerValue(String name, Integer value, Integer minimum, Integer maximum) {
        super(name, value);
        this.minimum = minimum;
        this.maximum = maximum;
    }
    
    /**
     * Constructor with description
     * @param name Value name
     * @param description Value description
     * @param value Default value
     * @param minimum Minimum value
     * @param maximum Maximum value
     */
    public IntegerValue(String name, String description, Integer value, Integer minimum, Integer maximum) {
        super(name, description, value);
        this.minimum = minimum;
        this.maximum = maximum;
    }
    
    /**
     * Sets the value, clamped to the valid range
     * @param value New value
     */
    @Override
    public void set(Integer value) {
        super.set(Math.max(minimum, Math.min(maximum, value)));
    }
    
    /**
     * Gets the minimum value
     * @return Minimum value
     */
    public Integer getMinimum() {
        return minimum;
    }
    
    /**
     * Gets the maximum value
     * @return Maximum value
     */
    public Integer getMaximum() {
        return maximum;
    }
    
    /**
     * Parses a string to an integer value
     * @param str String to parse
     * @return Whether the parsing was successful
     */
    public boolean fromString(String str) {
        try {
            set(Integer.parseInt(str));
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
