package com.networkedassets.git4c.standalone

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val plugin = StandalonePlugin()
        MainServer(plugin).start()
    }
}