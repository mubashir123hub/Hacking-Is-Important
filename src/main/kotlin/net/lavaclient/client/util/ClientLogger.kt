/*
 * This file is part of LavaClient.
 * https://github.com/lavaclient/client
 */
package net.lavaclient.client.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Logger utility for the client
 */
object ClientLogger {
    private val dateFormat = SimpleDateFormat("HH:mm:ss")
    private val logFile = java.io.File("logs/lavaclient.log")
    
    init {
        // Create logs directory if it doesn't exist
        logFile.parentFile?.mkdirs()
        
        // Clear log file on startup
        logFile.writeText("")
    }
    
    /**
     * Log an info message
     *
     * @param message The message to log
     */
    fun info(message: String) {
        log(LogLevel.INFO, message)
    }
    
    /**
     * Log a warning message
     *
     * @param message The message to log
     */
    fun warn(message: String) {
        log(LogLevel.WARN, message)
    }
    
    /**
     * Log an error message
     *
     * @param message The message to log
     * @param throwable Optional throwable to log
     */
    fun error(message: String, throwable: Throwable? = null) {
        log(LogLevel.ERROR, message)
        
        // Log throwable if provided
        throwable?.let {
            val stackTrace = it.stackTraceToString()
            log(LogLevel.ERROR, stackTrace)
        }
    }
    
    /**
     * Log a debug message
     *
     * @param message The message to log
     */
    fun debug(message: String) {
        log(LogLevel.DEBUG, message)
    }
    
    /**
     * Log a message with a specific level
     *
     * @param level The log level
     * @param message The message to log
     */
    private fun log(level: LogLevel, message: String) {
        // Format log message
        val time = dateFormat.format(Date())
        val formattedMessage = "[$time] [${level.name}] $message"
        
        // Print to console
        when (level) {
            LogLevel.INFO -> println(formattedMessage)
            LogLevel.WARN -> System.err.println(formattedMessage)
            LogLevel.ERROR -> System.err.println(formattedMessage)
            LogLevel.DEBUG -> println(formattedMessage)
        }
        
        // Write to log file
        try {
            logFile.appendText("$formattedMessage\n")
        } catch (e: Exception) {
            System.err.println("Failed to write to log file: ${e.message}")
        }
    }
    
    /**
     * Log levels
     */
    enum class LogLevel {
        INFO,
        WARN,
        ERROR,
        DEBUG
    }
}