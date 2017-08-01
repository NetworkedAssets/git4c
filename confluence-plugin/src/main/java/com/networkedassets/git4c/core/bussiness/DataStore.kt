package com.networkedassets.git4c.core.bussiness

interface DataStore<T> {
    fun isAvailable(id: String): Boolean
    fun get(id: String): T?
    fun put(id: String, data: T)
    fun remove(id: String)
    fun removeAll()
}