package com.networkedassets.git4c.infrastructure

import com.atlassian.cache.CacheFactory
import com.networkedassets.git4c.boundary.outbound.TemporaryBranch
import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.datastore.cache.TemporaryEditBranchResultCache
import com.networkedassets.git4c.infrastructure.cache.ShortConfluenceCache

class ConfluenceTemporaryEditBranchResultCache(cacheFactory: CacheFactory) : TemporaryEditBranchResultCache, ShortConfluenceCache<Computation<TemporaryBranch>>(cacheFactory)
