/*
 * This file is part of LavaClient.
 * https://github.com/lavaclient/client
 */
package net.lavaclient.client.account

import java.util.*

/**
 * Represents a Minecraft account
 */
data class Account(
    val uuid: UUID,
    val username: String,
    val type: AccountType,
    val accessToken: String = "",
    val clientToken: String = "",
    val lastLoginTime: Long = 0
) {
    /**
     * Check if the account is a premium (paid) account
     */
    fun isPremium(): Boolean = type != AccountType.OFFLINE
    
    companion object {
        /**
         * Create an offline account with the given username
         *
         * @param username The username for the offline account
         * @return A new offline account
         */
        fun createOfflineAccount(username: String): Account {
            // For offline accounts, create a UUID based on the username
            val uuid = UUID.nameUUIDFromBytes("OfflinePlayer:$username".toByteArray())
            return Account(
                uuid = uuid,
                username = username,
                type = AccountType.OFFLINE
            )
        }
    }
}

/**
 * Types of Minecraft accounts
 */
enum class AccountType {
    /**
     * Microsoft account (current account type)
     */
    MICROSOFT,
    
    /**
     * Legacy Mojang account
     */
    MOJANG,
    
    /**
     * Offline (cracked) account
     */
    OFFLINE
}