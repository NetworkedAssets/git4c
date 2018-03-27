package com.networkedassets.git4c.infrastructure.cache

import com.atlassian.cache.CacheFactory
import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.bussiness.ComputationCache

class AtlassianComputationCache<R : Any>(cacheFactory: CacheFactory) : ShortConfluenceCache<Computation<R>>(cacheFactory), ComputationCache<R>