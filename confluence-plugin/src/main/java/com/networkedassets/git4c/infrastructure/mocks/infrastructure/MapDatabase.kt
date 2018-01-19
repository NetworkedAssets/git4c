package com.networkedassets.git4c.infrastructure.mocks.infrastructure

import com.networkedassets.git4c.core.bussiness.Database

open class MapDatabase<T> : Database<T> {

    private val map = mutableMapOf<String, T>()

    override fun isAvailable(uuid: String) = map.containsKey(uuid)

    override fun get(uuid: String) = map[uuid]

    override fun getAll() = map.values.toList()

    override fun put(uuid: String, data: T) {
        map[uuid] = data
    }

    override fun remove(uuid: String) {
        map.remove(uuid)
    }

    override fun removeAll() {
        map.clear()
    }
}