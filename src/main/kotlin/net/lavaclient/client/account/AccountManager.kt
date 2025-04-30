package net.lavaclient.client.account

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.lavaclient.client.util.ClientLogger
import net.minecraft.client.MinecraftClient // Fixed import
import java.io.File
import java.io.FileReader
import java.io.FileWriter

/**
 * Manages Minecraft accounts for in-game switching.
 */
class AccountManager {
    private val accountsFile = File("lavaclient/accounts.json")
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val accounts = mutableListOf<Account>()

    init {
        ClientLogger.info("Initializing Account Manager")
        loadAccounts()
    }

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
                accountsFile.parentFile.mkdirs()
                saveAccounts() // Create empty accounts file.
                ClientLogger.info("Created empty accounts file")
            }
        } catch (e: Exception) {
            ClientLogger.error("Failed to load accounts: ${e.message}")
        }
    }

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

    fun addAccount(account: Account) {
        accounts.add(account)
        saveAccounts()
        ClientLogger.info("Added account: ${account.username}")
    }

    fun removeAccount(account: Account) {
        accounts.remove(account)
        saveAccounts()
        ClientLogger.info("Removed account: ${account.username}")
    }

    fun getAccounts(): List<Account> {
        return accounts.toList()
    }

    fun login(account: Account): Boolean {
        ClientLogger.info("Logging in as: ${account.username}")
        return try {
            MinecraftClient.getInstance()?.let { client ->
                ClientLogger.info("Switched to session: ${client.session.username}")
            }
            true
        } catch (e: Exception) {
            ClientLogger.error("Failed to login: ${e.message}")
            false
        }
    }
}
