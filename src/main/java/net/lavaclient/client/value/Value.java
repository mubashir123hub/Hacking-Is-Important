package net.lavaclient.client.value;

/**
 * Generic value class for module settings
 * @param <T> Value type
 */
public abstract class Value<T> {
    // Value data
    private final String name;
    private final String description;
    private T value;
    
    // Visibility condition (if any)
    private ValueCondition<Boolean> visibilityCondition;
    
    /**
     * Constructor with name only
     * @param name Value name
     * @param value Default value
     */
    public Value(String name, T value) {
        this(name, "", value);
    }
    
    /**
     * Constructor with name and description
     * @param name Value name
     * @param description Value description
     * @param value Default value
     */
    public Value(String name, String description, T value) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.visibilityCondition = null;
    }
    
    /**
     * Gets the value name
     * @return Value name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the value description
     * @return Value description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Gets the current value
     * @return Current value
     */
    public T get() {
        return value;
    }
    
    /**
     * Sets the value
     * @param value New value
     */
    public void set(T value) {
        this.value = value;
    }
    
    /**
     * Sets a visibility condition
     * @param condition The condition
     * @return This value for chaining
     */
    public Value<T> visibleWhen(ValueCondition<Boolean> condition) {
        this.visibilityCondition = condition;
        return this;
    }
    
    /**
     * Checks if the value should be visible
     * @return Whether the value should be visible
     */
    public boolean isVisible() {
        if (visibilityCondition == null) {
            return true;
        }
        
        return visibilityCondition.check();
    }
    
    /**
     * Functional interface for value conditions
     * @param <T> Condition result type
     */
    @FunctionalInterface
    public interface ValueCondition<T> {
        T check();
    }
}
