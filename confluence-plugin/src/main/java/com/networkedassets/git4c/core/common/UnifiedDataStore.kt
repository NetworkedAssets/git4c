package com.networkedassets.git4c.core.common

import com.networkedassets.git4c.core.bussiness.Cache
import com.networkedassets.git4c.core.bussiness.Database


class UnifiedDataStore<T>(val repository: Database<T>, val cache: Cache<T>) : Database<T> {

    override fun update(uuid: String, data: T) {
        repository.update(uuid, data)
        cache.update(uuid, data)
    }

    override fun isAvailable(uuid: String) = cache.isAvailable(uuid) || repository.isAvailable(uuid)

    override fun get(uuid: String): T? = cache.get(uuid) ?: repository.get(uuid)?.apply { cache.update(uuid, this) }

    override fun getAll(): List<T> = cache.getAll()

    override fun insert(uuid: String, data: T) {
        repository.insert(uuid, data)
        cache.insert(uuid, data)
    }

    override fun remove(uuid: String) {
        repository.remove(uuid)
        cache.remove(uuid)
    }

    override fun removeAll() {
        repository.removeAll()
        cache.removeAll()
    }
}
