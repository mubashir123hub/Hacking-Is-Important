/*
 * This file is part of LavaClient.
 * https://github.com/lavaclient/client
 */
package net.lavaclient.client.account

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.lavaclient.client.util.ClientLogger
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.Session
import net.minecraft.client.util.DefaultClientSession
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*

/**
 * Manages Minecraft accounts for in-game switching
 */
class AccountManager {
    private val accountsFile = File("lavaclient/accounts.json")
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val accounts = mutableListOf<Account>()
    
    init {
        ClientLogger.info("Initializing account manager")
        loadAccounts()
    }
    
    /**
     * Load accounts from file
     */
    private fun loadAccounts() {
        try {
            if (accountsFile.exists()) {
                FileReader(accountsFile).use { reader ->
                    val type = object : TypeToken<List<Account>>() {}.type
                    val loadedAccounts: List<Account> = gson.fromJson(reader, type)
                    accounts.clear()
                    accounts.addAll(loadedAccounts)
                    ClientLogger.info("Loaded ${accounts.size} accounts")
                }
            } else {
                // Create directory if it doesn't exist
                accountsFile.parentFile.mkdirs()
                saveAccounts() // Create empty file
                ClientLogger.info("Created empty accounts file")
            }
        } catch (e: Exception) {
            ClientLogger.error("Failed to load accounts: ${e.message}")
        }
    }
    
    /**
     * Save accounts to file
     */
    private fun saveAccounts() {
        try {
            accountsFile.parentFile.mkdirs()
            FileWriter(accountsFile).use { writer ->
                gson.toJson(accounts, writer)
            }
            ClientLogger.info("Saved ${accounts.size} accounts")
        } catch (e: Exception) {
            ClientLogger.error("Failed to save accounts: ${e.message}")
        }
    }
    
    /**
     * Add an account
     *
     * @param account The account to add
     */
    fun addAccount(account: Account) {
        accounts.add(account)
        saveAccounts()
        ClientLogger.info("Added account: ${account.username}")
    }
    
    /**
     * Remove an account
     *
     * @param account The account to remove
     */
    fun removeAccount(account: Account) {
        accounts.remove(account)
        saveAccounts()
        ClientLogger.info("Removed account: ${account.username}")
    }
    
    /**
     * Get all accounts
     *
     * @return A list of all accounts
     */
    fun getAccounts(): List<Account> {
        return accounts.toList()
    }
    
    /**
     * Login to an account
     *
     * @param account The account to login to
     * @return True if login successful, false otherwise
     */
    fun login(account: Account): Boolean {
        // This is a simplified version - actual implementation would involve authentication
        ClientLogger.info("Logging in as: ${account.username}")
        
        try {
            // Here you would implement the actual account switching logic
            // For now, this is just a placeholder
            // In a real client, this would interact with Minecraft's session system
            
            // For testing purposes:
            MinecraftClient.getInstance().session?.let { session ->
                ClientLogger.info("Current session: ${session.username}")
            }
            
            return true
        } catch (e: Exception) {
            ClientLogger.error("Failed to login: ${e.message}")
            return false
        }
    }
}
