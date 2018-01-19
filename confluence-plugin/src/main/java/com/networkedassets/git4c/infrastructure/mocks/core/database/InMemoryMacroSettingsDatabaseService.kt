package com.networkedassets.git4c.infrastructure.mocks.core.database

import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.infrastructure.mocks.generic.InMemoryCache

class InMemoryMacroSettingsDatabaseService : MacroSettingsDatabase, InMemoryCache<MacroSettings>() {
    override fun getByRepository(uuid: String): List<MacroSettings> {
        return super.map.filter { it.value.repositoryUuid == uuid }.values.toList()
    }
}
