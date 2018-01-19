package com.networkedassets.git4c.core.datastore

import com.networkedassets.git4c.core.common.UnifiedDataStore
import com.networkedassets.git4c.core.datastore.cache.GlobForMacroCache
import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.data.GlobForMacro

class GlobForMacroProvider(val globForMacroDatabase: GlobForMacroDatabase, val globForMacroCache: GlobForMacroCache) : GlobForMacroDatabase {
    val dataStoreSplitter = UnifiedDataStore(globForMacroDatabase,globForMacroCache)

    override fun getByMacro(macroUuid: String): List<GlobForMacro> {
        val result = globForMacroCache.getByMacro(macroUuid)
        return if (result.isNotEmpty()) result else globForMacroDatabase.getByMacro(macroUuid)
    }

    override fun put(uuid: String, data: GlobForMacro) {
        dataStoreSplitter.remove(uuid)
        dataStoreSplitter.put(uuid, data)
    }

    override fun isAvailable(uuid: String): Boolean = dataStoreSplitter.isAvailable(uuid)

    override fun get(uuid: String): GlobForMacro? = dataStoreSplitter.get(uuid)

    override fun getAll(): List<GlobForMacro> = dataStoreSplitter.getAll()

    override fun remove(uuid: String) {
        dataStoreSplitter.remove(uuid)
    }

    override fun removeAll() {
        dataStoreSplitter.removeAll()
    }
}