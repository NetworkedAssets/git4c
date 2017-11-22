package com.networkedassets.git4c.core.datastore

interface MacroIdToSpaceAndPageDatabase {

    /**
     * @return [spaceId, pageId]
     */
    fun getSpaceAndPageForMacro(macroId: String): Pair<String, String>?

    /**
     * @param pair [spaceId, pageId]
     */
    fun putStaceAndPageForMacro(macroId: String, pair: Pair<String, String>)

    fun clear()

}