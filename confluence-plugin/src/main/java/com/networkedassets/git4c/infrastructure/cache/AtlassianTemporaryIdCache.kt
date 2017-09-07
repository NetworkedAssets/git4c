package com.networkedassets.git4c.infrastructure.cache

import com.atlassian.cache.CacheFactory
import com.networkedassets.git4c.core.datastore.cache.TemporaryIdCache

class AtlassianTemporaryIdCache(cacheFactory: CacheFactory) : ConfluenceCache<String>(cacheFactory), TemporaryIdCache