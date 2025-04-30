package net.lavaclient.client.utils;

/**
 * Utility class for timing operations
 */
public class TimerUtils {
    private long lastMS;
    
    /**
     * Constructor initializes the timer
     */
    public TimerUtils() {
        this.reset();
    }
    
    /**
     * Gets the current time in milliseconds
     * @return Current time
     */
    public static long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }
    
    /**
     * Resets the timer
     */
    public void reset() {
        this.lastMS = getCurrentMS();
    }
    
    /**
     * Gets the time elapsed since the last reset
     * @return Elapsed time in milliseconds
     */
    public long getTime() {
        return getCurrentMS() - this.lastMS;
    }
    
    /**
     * Gets the number of iterations based on time
     * @param delay Delay in milliseconds
     * @return Number of iterations
     */
    public long getIterations(long delay) {
        long time = getTime();
        
        if (time < delay) {
            return 0;
        }
        
        return (time / delay);
    }
    
    /**
     * Gets time left until the specified delay
     * @param delay Delay in milliseconds
     * @return Time left in milliseconds
     */
    public long getTimeLeft(long delay) {
        long time = getTime();
        
        if (time > delay) {
            return 0;
        }
        
        return delay - time;
    }
    
    /**
     * Checks if the specified time has passed
     * @param delay Delay in milliseconds
     * @return Whether the time has passed
     */
    public boolean hasTimePassed(long delay) {
        return getCurrentMS() - this.lastMS >= delay;
    }
    
    /**
     * Checks if the specified time has passed and resets the timer if it has
     * @param delay Delay in milliseconds
     * @return Whether the time has passed
     */
    public boolean hasTimeElapsed(long delay) {
        if (getCurrentMS() - this.lastMS > delay) {
            this.reset();
            return true;
        }
        
        return false;
    }
    
    /**
     * Adds time to the timer
     * @param time Time to add in milliseconds
     */
    public void addTime(long time) {
        this.lastMS += time;
    }
    
    /**
     * Sets the timer to a specific time
     * @param time Time in milliseconds
     */
    public void setTime(long time) {
        this.lastMS = time;
    }
    
    /**
     * Gets the last reset time
     * @return Last reset time
     */
    public long getLastMS() {
        return lastMS;
    }
    
    /**
     * Calculates the rate for time-based operations
     * @param delay Delay in milliseconds
     * @return Rate between 0.0 and 1.0
     */
    public float getTimerRate(long delay) {
        long time = getTime();
        
        if (time > delay) {
            return 1.0f;
        }
        
        return (float) time / delay;
    }
}
