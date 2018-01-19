package com.networkedassets.git4c.core.datastore.cache

import com.networkedassets.git4c.core.bussiness.Cache
import com.networkedassets.git4c.data.GlobForMacro

interface GlobForMacroCache : Cache<GlobForMacro> {
    fun getByMacro(macroUuid: String): List<GlobForMacro>
}