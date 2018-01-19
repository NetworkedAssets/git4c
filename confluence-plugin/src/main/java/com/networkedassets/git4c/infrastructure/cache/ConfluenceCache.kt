package com.networkedassets.git4c.infrastructure.cache

import com.atlassian.cache.CacheFactory
import com.atlassian.cache.CacheSettings
import com.atlassian.cache.CacheSettingsBuilder
import com.networkedassets.git4c.core.bussiness.Cache
import java.util.concurrent.TimeUnit

abstract class LongConfluenceCache<T : Any>(cacheFactory: CacheFactory, maxEntries: Int) : ConfluenceCache<T>(cacheFactory, CacheSettingsBuilder().expireAfterAccess(10, TimeUnit.DAYS).maxEntries(maxEntries).build())
abstract class ShortConfluenceCache<T : Any>(cacheFactory: CacheFactory) : ConfluenceCache<T>(cacheFactory, CacheSettingsBuilder().expireAfterAccess(1, TimeUnit.HOURS).build())
abstract class MinuteConfluenceCache<T : Any>(cacheFactory: CacheFactory) : ConfluenceCache<T>(cacheFactory, CacheSettingsBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build())

abstract class ConfluenceCache<T : Any>(cacheFactory: CacheFactory, expireAfter: CacheSettings) : Cache<T> {

    val cache = cacheFactory.getCache<String, T>("${this::class.java.name}.cache", null, expireAfter)

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


    override fun put(uuid: String, data: T) {
        cache.put(uuid, data)
    }

    override fun remove(uuid: String) {
        cache.remove(uuid)
    }

    override fun removeAll() {
        cache.removeAll()
    }
}