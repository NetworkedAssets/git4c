package com.networkedassets.git4c.infrastructure

import com.atlassian.cache.CacheFactory
import com.networkedassets.git4c.core.datastore.TemporaryIdCache

class AtlassianTemporaryIdCache(cacheFactory: CacheFactory): ConfluenceCache<String>(cacheFactory), TemporaryIdCache