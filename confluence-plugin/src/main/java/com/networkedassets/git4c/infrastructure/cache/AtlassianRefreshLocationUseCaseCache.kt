package com.networkedassets.git4c.infrastructure.cache

import com.atlassian.cache.CacheFactory
import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.datastore.cache.RefreshLocationUseCaseCache

class AtlassianRefreshLocationUseCaseCache(cacheFactory: CacheFactory): ShortConfluenceCache<Computation<Unit>>(cacheFactory), RefreshLocationUseCaseCache