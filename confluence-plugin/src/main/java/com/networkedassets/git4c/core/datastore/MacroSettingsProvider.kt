package com.networkedassets.git4c.core.datastore

import com.networkedassets.git4c.core.common.UnifiedDataStore
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.data.MacroSettings

class MacroSettingsProvider(val dataStoreSplitter: UnifiedDataStore<MacroSettings>, val macroSettingsDatabase: MacroSettingsDatabase) : MacroSettingsDatabase {

    override fun getByRepository(uuid: String): List<MacroSettings> {
        return macroSettingsDatabase.getByRepository(uuid)
    }

    override fun put(uuid: String, data: MacroSettings) {
        dataStoreSplitter.remove(uuid)
        dataStoreSplitter.put(uuid, data)
    }

    override fun isAvailable(uuid: String): Boolean = dataStoreSplitter.isAvailable(uuid)

    override fun get(uuid: String): MacroSettings? = dataStoreSplitter.get(uuid)

    override fun getAll(): List<MacroSettings> = dataStoreSplitter.getAll()

    override fun remove(uuid: String) {
        dataStoreSplitter.remove(uuid)
    }

    override fun removeAll() {
        dataStoreSplitter.removeAll()
    }
}