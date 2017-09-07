package com.networkedassets.git4c.infrastructure.cache

import com.atlassian.cache.CacheFactory
import com.networkedassets.git4c.core.datastore.cache.MacroSettingsCache
import com.networkedassets.git4c.data.MacroSettings


class AtlassianMacroSettingsCache(cacheFactory: CacheFactory) : ConfluenceCache<MacroSettings>(cacheFactory), MacroSettingsCache
