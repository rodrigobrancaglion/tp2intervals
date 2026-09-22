package org.freekode.tp2intervals.config.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AppLogger(private val logger: Logger) {

    companion object {
        inline fun <reified T> get(): AppLogger {
            return AppLogger(LoggerFactory.getLogger(T::class.java))
        }

        // Allows instantiation passing dynamic class (used in Aspect)
        fun get(clazz: Class<*>): AppLogger {
            return AppLogger(LoggerFactory.getLogger(clazz))
        }

        // Allows instantiation passing a native SLF4J Logger
        fun get(logger: Logger): AppLogger {
            return AppLogger(logger)
        }
    }

    // Info convenience methods
    fun infoL1In(message: String, vararg args: Any?) = info(LogIndent.L1_IN, message, *args)
    fun infoL1Out(message: String, vararg args: Any?) = info(LogIndent.L1_OUT, message, *args)
    fun infoL2In(message: String, vararg args: Any?) = info(LogIndent.L2_IN, message, *args)
    fun infoL2Out(message: String, vararg args: Any?) = info(LogIndent.L2_OUT, message, *args)
    fun infoL3In(message: String, vararg args: Any?) = info(LogIndent.L3_IN, message, *args)
    fun warnL3In(message: String, vararg args: Any?) = warn(LogIndent.L3_IN, message, *args)
    fun infoL4In(message: String, vararg args: Any?) = info(LogIndent.L4_IN, message, *args)
    fun infoL4Out(message: String, vararg args: Any?) = info(LogIndent.L4_OUT, message, *args)

    // Error convenience methods
    fun errorL1In(message: String, vararg args: Any?) = error(LogIndent.L1_IN, message, *args)
    fun errorL1Out(message: String, vararg args: Any?) = error(LogIndent.L1_OUT, message, *args)
    fun errorL2In(message: String, vararg args: Any?) = error(LogIndent.L2_IN, message, *args)
    fun errorL3In(message: String, vararg args: Any?) = error(LogIndent.L3_IN, message, *args)
    fun errorL4In(message: String, vararg args: Any?) = error(LogIndent.L4_IN, message, *args)
    fun errorL2Out(message: String, vararg args: Any?) = error(LogIndent.L2_OUT, message, *args)

    fun debugL3In(message: String, vararg args: Any?) = debug(LogIndent.L3_IN, message, *args)

    // Main method parameterized by LogIndent
    fun info(indent: LogIndent, message: String, vararg args: Any?) {
        if (logger.isInfoEnabled) {
            logger.info("${indent.prefix}$message", *args)
        }
    }

    fun warn(indent: LogIndent, message: String, vararg args: Any?) {
        if (logger.isInfoEnabled) {
            logger.warn("${indent.prefix}$message", *args)
        }
    }

    fun debug(indent: LogIndent = LogIndent.NONE, message: String, vararg args: Any?) {
        if (logger.isDebugEnabled) {
            logger.debug("${indent.prefix}$message", *args)
        }
    }

    fun error(indent: LogIndent = LogIndent.NONE, message: String, vararg args: Any?, throwable: Throwable? = null) {
        if (logger.isErrorEnabled) {
            if (throwable != null) {
                logger.error("${indent.prefix}$message", *args, throwable)
            } else {
                logger.error("${indent.prefix}$message", *args)
            }
        }
    }

}