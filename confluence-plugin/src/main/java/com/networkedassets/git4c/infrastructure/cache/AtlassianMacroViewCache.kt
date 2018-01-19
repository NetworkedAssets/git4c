package com.networkedassets.git4c.infrastructure.cache

import com.atlassian.cache.CacheFactory
import com.networkedassets.git4c.core.datastore.cache.MacroToBeViewedPrepareLockCache
import com.networkedassets.git4c.data.MacroView


class AtlassianMacroViewCache(cacheFactory: CacheFactory) : ShortConfluenceCache<MacroView>(cacheFactory), MacroToBeViewedPrepareLockCache
