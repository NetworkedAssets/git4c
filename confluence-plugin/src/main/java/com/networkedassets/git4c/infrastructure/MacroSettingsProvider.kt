package com.networkedassets.git4c.infrastructure

import com.networkedassets.git4c.core.datastore.MacroSettingsRepository
import com.networkedassets.git4c.data.macro.DocumentationsMacroSettings

class MacroSettingsProvider(val dataStoreSplitter: UnifiedDataStore<DocumentationsMacroSettings>) : MacroSettingsRepository {
    override fun isAvailable(id: String): Boolean = dataStoreSplitter.isAvailable(id)

    override fun get(id: String): DocumentationsMacroSettings? = dataStoreSplitter.get(id)

    override fun put(id: String, data: DocumentationsMacroSettings) {
        dataStoreSplitter.put(id, data)
    }

    override fun remove(id: String) {
        dataStoreSplitter.remove(id)
    }

    override fun removeAll() {
        dataStoreSplitter.removeAll()
    }
}