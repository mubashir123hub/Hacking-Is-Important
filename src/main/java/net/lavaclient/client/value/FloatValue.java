package net.lavaclient.client.value;

/**
 * Float value class for module settings
 */
public class FloatValue extends Value<Float> {
    private final float minimum;
    private final float maximum;
    
    /**
     * Constructor
     * @param name Value name
     * @param value Default value
     * @param minimum Minimum value
     * @param maximum Maximum value
     */
    public FloatValue(String name, Float value, Float minimum, Float maximum) {
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
    public FloatValue(String name, String description, Float value, Float minimum, Float maximum) {
        super(name, description, value);
        this.minimum = minimum;
        this.maximum = maximum;
    }
    
    /**
     * Sets the value, clamped to the valid range
     * @param value New value
     */
    @Override
    public void set(Float value) {
        super.set(Math.max(minimum, Math.min(maximum, value)));
    }
    
    /**
     * Gets the minimum value
     * @return Minimum value
     */
    public Float getMinimum() {
        return minimum;
    }
    
    /**
     * Gets the maximum value
     * @return Maximum value
     */
    public Float getMaximum() {
        return maximum;
    }
    
    /**
     * Parses a string to a float value
     * @param str String to parse
     * @return Whether the parsing was successful
     */
    public boolean fromString(String str) {
        try {
            set(Float.parseFloat(str));
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
