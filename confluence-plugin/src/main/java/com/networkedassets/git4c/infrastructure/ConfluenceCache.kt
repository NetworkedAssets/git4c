package com.networkedassets.git4c.infrastructure

import com.atlassian.cache.CacheFactory
import com.atlassian.cache.CacheSettingsBuilder
import com.networkedassets.git4c.core.bussiness.Cache
import java.util.concurrent.TimeUnit

abstract class ConfluenceCache<T>(cacheFactory: CacheFactory): Cache<T> {

    val cache = cacheFactory.getCache<String, T>("${this::class.java.name}.cache", null, CacheSettingsBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build())

    init {
        cache.removeAll()
    }

    override fun isAvailable(id: String): Boolean {
        return cache.get(id) != null
    }

    override fun get(id: String): T? {
        val value = cache.get(id)
        return value
    }

    override fun put(id: String, data: T) {
        cache.put(id, data)
    }

    override fun remove(id: String) {
        cache.remove(id)
    }

    override fun removeAll() {
        cache.removeAll()
    }
}