package com.networkedassets.git4c.infrastructure.cache

import com.atlassian.cache.CacheFactory
import com.networkedassets.git4c.core.datastore.cache.GlobForMacroCache
import com.networkedassets.git4c.data.GlobForMacro

class AtlassianGlobForMacroCache(cacheFactory: CacheFactory) : ShortConfluenceCache<GlobForMacro>(cacheFactory), GlobForMacroCache {
    override fun getByMacro(macroUuid: String): List<GlobForMacro> = getAll().filter { it.macroSettingsUuid == macroUuid }
}