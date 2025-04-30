package net.lavaclient.client.account;

import com.google.gson.annotations.Expose;

/**
 * Class representing a Minecraft account
 */
public class Account {
    // Account information
    @Expose
    private final String username;
    
    // Not exposed to JSON for security
    private final String password;
    
    @Expose
    private final String uuid;
    
    @Expose
    private final String accessToken;
    
    @Expose
    private final AccountType type;
    
    // Last login timestamp
    @Expose
    private long lastLogin;
    
    /**
     * Constructor
     * @param username Username
     * @param password Password
     * @param uuid UUID
     * @param accessToken Access token
     * @param type Account type
     */
    public Account(String username, String password, String uuid, String accessToken, AccountType type) {
        this.username = username;
        this.password = password;
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.type = type;
        this.lastLogin = System.currentTimeMillis();
    }
    
    /**
     * Gets the username
     * @return Username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Gets the password
     * @return Password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Gets the UUID
     * @return UUID
     */
    public String getUuid() {
        return uuid;
    }
    
    /**
     * Gets the access token
     * @return Access token
     */
    public String getAccessToken() {
        return accessToken;
    }
    
    /**
     * Gets the account type
     * @return Account type
     */
    public AccountType getType() {
        return type;
    }
    
    /**
     * Gets the last login timestamp
     * @return Last login timestamp
     */
    public long getLastLogin() {
        return lastLogin;
    }
    
    /**
     * Sets the last login timestamp
     * @param lastLogin New last login timestamp
     */
    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    /**
     * Updates the last login timestamp to now
     */
    public void updateLastLogin() {
        this.lastLogin = System.currentTimeMillis();
    }
    
    /**
     * Gets a display name for the account
     * @return Display name
     */
    public String getDisplayName() {
        return username + " (" + type.name() + ")";
    }
    
    /**
     * Account types
     */
    public enum AccountType {
        /**
         * Regular Mojang account
         */
        MOJANG,
        
        /**
         * Microsoft account
         */
        MICROSOFT,
        
        /**
         * Cracked/offline account
         */
        OFFLINE
    }
    
    /**
     * Checks if the account has a password
     * @return Whether the account has a password
     */
    public boolean hasPassword() {
        return password != null && !password.isEmpty();
    }
    
    /**
     * Checks if the account has session information (token and UUID)
     * @return Whether the account has session information
     */
    public boolean hasSession() {
        return accessToken != null && !accessToken.isEmpty() && uuid != null && !uuid.isEmpty();
    }
    
    /**
     * String representation
     * @return String representation
     */
    @Override
    public String toString() {
        return getDisplayName();
    }
    
    /**
     * Equals method
     * @param obj Object to compare
     * @return Whether the objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        Account account = (Account) obj;
        return username.equalsIgnoreCase(account.username);
    }
    
    /**
     * Hash code method
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return username.toLowerCase().hashCode();
    }
}
