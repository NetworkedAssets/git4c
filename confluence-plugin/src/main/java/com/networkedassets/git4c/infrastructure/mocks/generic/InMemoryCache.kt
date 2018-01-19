package com.networkedassets.git4c.infrastructure.mocks.generic

import com.networkedassets.git4c.core.bussiness.Cache

open class InMemoryCache<T> : Cache<T> {

    val map = HashMap<String, T>()
    override fun get(uuid: String): T? {
        return map.get(uuid)
    }

    override fun getAll(): List<T> {
        return map.values.toList()
    }

    override fun put(uuid: String, data: T) {
        map.put(uuid, data)
    }

    override fun remove(uuid: String) {
        map.remove(uuid)
    }

    override fun removeAll() {
        map.clear()
    }

    override fun isAvailable(uuid: String): Boolean {
        return get(uuid) != null
    }

}