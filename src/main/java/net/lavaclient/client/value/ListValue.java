package net.lavaclient.client.value;

import java.util.Arrays;
import java.util.List;

/**
 * List value class for module settings that cycle through options
 */
public class ListValue extends Value<String> {
    private final List<String> values;
    
    /**
     * Constructor
     * @param name Value name
     * @param values Possible values
     * @param value Default value
     */
    public ListValue(String name, String[] values, String value) {
        super(name, value);
        this.values = Arrays.asList(values);
        
        // Verify the value is in the list
        if (!this.values.contains(value)) {
            set(this.values.get(0));
        }
    }
    
    /**
     * Constructor with description
     * @param name Value name
     * @param description Value description
     * @param values Possible values
     * @param value Default value
     */
    public ListValue(String name, String description, String[] values, String value) {
        super(name, description, value);
        this.values = Arrays.asList(values);
        
        // Verify the value is in the list
        if (!this.values.contains(value)) {
            set(this.values.get(0));
        }
    }
    
    /**
     * Sets the value, ensuring it's in the list
     * @param value New value
     */
    @Override
    public void set(String value) {
        if (values.contains(value)) {
            super.set(value);
        }
    }
    
    /**
     * Gets all possible values
     * @return List of possible values
     */
    public List<String> getValues() {
        return values;
    }
    
    /**
     * Gets the index of the current value
     * @return Current value index
     */
    public int getIndex() {
        return values.indexOf(get());
    }
    
    /**
     * Selects the next value in the list
     * @return New value
     */
    public String selectNext() {
        int index = getIndex();
        
        if (index == -1) {
            set(values.get(0));
        } else {
            set(values.get((index + 1) % values.size()));
        }
        
        return get();
    }
    
    /**
     * Selects the previous value in the list
     * @return New value
     */
    public String selectPrevious() {
        int index = getIndex();
        
        if (index == -1) {
            set(values.get(0));
        } else {
            set(values.get((index - 1 + values.size()) % values.size()));
        }
        
        return get();
    }
    
    /**
     * Parses a string to set the value
     * @param str String to parse
     * @return Whether the parsing was successful
     */
    public boolean fromString(String str) {
        if (values.contains(str)) {
            set(str);
            return true;
        }
        
        return false;
    }
}
