package com.networkedassets.git4c.infrastructure

import com.atlassian.cache.CacheFactory
import com.networkedassets.git4c.boundary.outbound.Spaces
import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.datastore.cache.SpacesWithMacroResultCache
import com.networkedassets.git4c.infrastructure.cache.ShortConfluenceCache

class ConfluenceSpacesWithMacroResultCache(cacheFactory: CacheFactory) : SpacesWithMacroResultCache, ShortConfluenceCache<Computation<Spaces>>(cacheFactory)