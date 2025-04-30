package net.lavaclient.client.account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.lavaclient.client.LavaClient;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager for Minecraft accounts
 */
public class AccountManager {
    // List of accounts
    private final List<Account> accounts = new ArrayList<>();
    
    // Current account
    private Account currentAccount;
    
    // File to store accounts
    private final File accountsFile;
    
    // Gson for serialization
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * Constructor
     */
    public AccountManager() {
        // Initialize the accounts file
        accountsFile = new File(LavaClient.getInstance().getClientDir(), "accounts.json");
        
        // Set the current account to the active one
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getSession() != null) {
            currentAccount = new Account(
                    mc.getSession().getUsername(),
                    "", // Password can't be retrieved
                    mc.getSession().getPlayerID(),
                    mc.getSession().getToken(),
                    Account.AccountType.MOJANG // Assume Mojang
            );
        }
    }
    
    /**
     * Loads accounts from file
     */
    public void loadAccounts() {
        // Clear the existing accounts
        accounts.clear();
        
        // If the file doesn't exist, return
        if (!accountsFile.exists()) {
            return;
        }
        
        try (Reader reader = new FileReader(accountsFile)) {
            // Load accounts from the file
            Type listType = new TypeToken<ArrayList<Account>>() {}.getType();
            List<Account> loadedAccounts = gson.fromJson(reader, listType);
            
            // Add all loaded accounts
            if (loadedAccounts != null) {
                accounts.addAll(loadedAccounts);
            }
        } catch (Exception e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
    }
    
    /**
     * Saves accounts to file
     */
    public void saveAccounts() {
        try (Writer writer = new FileWriter(accountsFile)) {
            // Save accounts to the file
            gson.toJson(accounts, writer);
        } catch (Exception e) {
            System.err.println("Error saving accounts: " + e.getMessage());
        }
    }
    
    /**
     * Adds an account
     * @param account The account to add
     */
    public void addAccount(Account account) {
        // Check if the account already exists
        for (Account existingAccount : accounts) {
            if (existingAccount.getUsername().equalsIgnoreCase(account.getUsername())) {
                // Remove the existing account
                accounts.remove(existingAccount);
                break;
            }
        }
        
        // Add the account
        accounts.add(account);
        
        // Save the accounts
        saveAccounts();
    }
    
    /**
     * Removes an account
     * @param account The account to remove
     */
    public void removeAccount(Account account) {
        accounts.remove(account);
        
        // Save the accounts
        saveAccounts();
    }
    
    /**
     * Gets all accounts
     * @return List of accounts
     */
    public List<Account> getAccounts() {
        return accounts;
    }
    
    /**
     * Gets the current account
     * @return Current account
     */
    public Account getCurrentAccount() {
        return currentAccount;
    }
    
    /**
     * Sets the current account
     * @param account New current account
     */
    public void setCurrentAccount(Account account) {
        this.currentAccount = account;
    }
    
    /**
     * Logs in to an account
     * @param account The account to log in to
     * @return Whether the login was successful
     */
    public boolean login(Account account) {
        if (account == null) {
            return false;
        }
        
        // For this example, we'll assume the login is successful
        // In a real implementation, you would use the Mojang API to log in
        
        // Set the current account
        setCurrentAccount(account);
        
        try {
            // In a real implementation, you would update the Minecraft session
            // This would involve using reflection to access the private fields
            // of the Minecraft class and setting the session
            
            // For this example, just indicate success
            return true;
        } catch (Exception e) {
            System.err.println("Error logging in: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Attempts to log in with username and password
     * @param username Username
     * @param password Password
     * @param type Account type
     * @return Whether the login was successful
     */
    public boolean login(String username, String password, Account.AccountType type) {
        // Create a temporary account
        Account account = new Account(username, password, "", "", type);
        
        // Attempt to log in
        boolean success = login(account);
        
        // If successful, add the account
        if (success) {
            addAccount(account);
        }
        
        return success;
    }
    
    /**
     * Gets an account by username
     * @param username Username to search for
     * @return The account or null if not found
     */
    public Account getAccountByUsername(String username) {
        for (Account account : accounts) {
            if (account.getUsername().equalsIgnoreCase(username)) {
                return account;
            }
        }
        
        return null;
    }
}
