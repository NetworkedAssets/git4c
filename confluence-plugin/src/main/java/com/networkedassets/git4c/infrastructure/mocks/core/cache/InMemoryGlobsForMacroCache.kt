package com.networkedassets.git4c.infrastructure.mocks.core.cache

import com.networkedassets.git4c.core.datastore.cache.GlobForMacroCache
import com.networkedassets.git4c.data.GlobForMacro
import com.networkedassets.git4c.infrastructure.mocks.generic.InMemoryCache

class InMemoryGlobsForMacroCache : GlobForMacroCache, InMemoryCache<GlobForMacro>() {
    override fun getByMacro(macroUuid: String): List<GlobForMacro> {
        return super.map.filter { it.value.macroSettingsUuid == macroUuid }.values.toList()
    }
}