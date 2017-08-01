package com.networkedassets.git4c.utils

import org.slf4j.LoggerFactory

/**
 * Use this to register anything that should happen, or be closed at application shutdown
 */
object Autocloser {

    private val log = LoggerFactory.getLogger(Autocloser::class.java)

    private val shutdownProcedures = mutableSetOf<() -> Unit>()

    /**
     * Registers an arbitrary action to be taken on application shutdown
     */
    fun atShutdownDo(block: () -> Unit) {
        shutdownProcedures += block
    }

    /**
     * Registers an object to be closed at application shutdown.
     *
     * Returns the object, allowing you to write
     * ```
     * val myObject: MyObject = Autocloser.registerToClose(MyObject(foo, bar))
     * ```
     */
    fun <T : AutoCloseable> registerToClose(closeable: T): T {
        shutdownProcedures += { closeable.close() }
        return closeable
    }

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            shutdownAll()
        })
    }

    /**
     * Runs all the registered shutdown procedures in an unspecified order.
     *
     * Safe to call multiple times - each registered shutdown procedure will be only fired once.
     * If not called, the procedures will fire anyway when JVM runtime shuts down
     */
    fun shutdownAll() {
        while (shutdownProcedures.size > 0) {
            val proc = shutdownProcedures.first()

            try {
                proc()
            } catch (e: Exception) {
                log.error(e) { "Problem during shutdown: " }
            }

            shutdownProcedures -= proc
        }
    }
}

