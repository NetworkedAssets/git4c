package com.networkedassets.git4c.infrastructure

import com.networkedassets.git4c.core.datastore.MacroIdToSpaceAndPageDatabase

class HashmapMacroIdToSpaceAndPageDatabase: MacroIdToSpaceAndPageDatabase {

    val map = mutableMapOf<String, Pair<String, String>>()

    override fun getSpaceAndPageForMacro(macroId: String): Pair<String, String>? {
        return map[macroId]
    }

    override fun putStaceAndPageForMacro(macroId: String, pair: Pair<String, String>) {
        map[macroId] = pair
    }

    override fun clear() {
        map.clear()
    }

}