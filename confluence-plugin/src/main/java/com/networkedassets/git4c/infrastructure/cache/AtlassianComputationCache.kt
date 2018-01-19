package com.networkedassets.git4c.infrastructure.cache

import com.atlassian.cache.CacheFactory
import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.datastore.cache.PublishFileComputationCache

class AtlassianComputationCache(cacheFactory: CacheFactory): ShortConfluenceCache<Computation<Unit>>(cacheFactory), PublishFileComputationCache