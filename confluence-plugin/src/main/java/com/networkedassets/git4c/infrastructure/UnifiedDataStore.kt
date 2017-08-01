package com.networkedassets.git4c.infrastructure

import com.networkedassets.git4c.core.bussiness.Cache
import com.networkedassets.git4c.core.bussiness.Database


class UnifiedDataStore<T>(val repository: Database<T>, val cache: Cache<T>) : Database<T> {

    override fun isAvailable(id: String) = cache.isAvailable(id) || repository.isAvailable(id)

    override fun get(id: String): T? = cache.get(id) ?: repository.get(id)

    override fun put(id: String, data: T) {
        repository.put(id, data)
    }

    override fun remove(id: String) {
        repository.remove(id)
        cache.remove(id)
    }

    override fun removeAll() {
        repository.removeAll()
        cache.removeAll()
    }
}
