package com.networkedassets.git4c.utils

import com.networkedassets.git4c.core.bussiness.Database

open class MapDatabase<T> : Database<T> {

    private val map = mutableMapOf<String, T>()

    override fun isAvailable(uuid: String) = map.containsKey(uuid)

    override fun get(uuid: String) = map[uuid]

    override fun getAll() = map.values.toList()

    override fun insert(uuid: String, data: T) {
        map[uuid] = data
    }

    override fun update(uuid: String, data: T) = insert(uuid, data)

    override fun remove(uuid: String) {
        map.remove(uuid)
    }

    override fun removeAll() {
        map.clear()
    }
}