package com.networkedassets.git4c.standalone.infrastructure

import com.networkedassets.git4c.core.bussiness.DataStore

abstract class HashMapDataSource<T> : DataStore<T> {

    val map = mutableMapOf<String, T>()

    override fun isAvailable(id: String) = map[id] != null

    override fun get(id: String) = map[id]

    override fun put(id: String, data: T): Unit {
        map.put(id, data)
    }

    override fun remove(id: String) {
        map.remove(id)
    }

    override fun removeAll() {
        map.clear()
    }
}