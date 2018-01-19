package com.networkedassets.git4c.infrastructure.mocks.core.database

import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.data.GlobForMacro
import com.networkedassets.git4c.infrastructure.mocks.generic.InMemoryCache

class InMemoryGlobsForMacroDatabaseService : GlobForMacroDatabase, InMemoryCache<GlobForMacro>() {
    override fun getByMacro(macroUuid: String): List<GlobForMacro> {
        return super.map.filter { it.value.macroSettingsUuid == macroUuid }.values.toList()
    }
}