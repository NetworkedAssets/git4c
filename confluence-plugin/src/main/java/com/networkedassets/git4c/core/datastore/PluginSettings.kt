package com.networkedassets.git4c.core.datastore


interface PluginSettings {
    fun put(key: String, setting: String)
    fun get(key: String): String?
    fun remove(key: String)
}