package com.networkedassets.git4c.infrastructure.cache

import com.atlassian.cache.CacheFactory
import com.atlassian.cache.CacheSettingsBuilder
import com.networkedassets.git4c.core.bussiness.Cache
import java.util.concurrent.TimeUnit

abstract class ConfluenceCache<T : Any>(cacheFactory: CacheFactory) : Cache<T> {

    val cache = cacheFactory.getCache<String, T>("${this::class.java.name}.cache", null, CacheSettingsBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build())

    init {
        cache.removeAll()
    }

    override fun isAvailable(uuid: String): Boolean {
        return cache.get(uuid) != null
    }

    override fun get(uuid: String): T? {
        val value = cache.get(uuid)
        return value
    }

    override fun getAll(): List<T> = cache.keys.map { cache.get(it) }.filterNotNull()


    override fun insert(uuid: String, data: T) {
        cache.put(uuid, data)
    }

    override fun remove(uuid: String) {
        cache.remove(uuid)
    }

    override fun removeAll() {
        cache.removeAll()
    }

    override fun update(uuid: String, data: T) {
        cache.remove(uuid)
        cache.put(uuid, data)
    }
}