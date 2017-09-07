package com.networkedassets.git4c.core.datastore.repositories

import com.atlassian.activeobjects.tx.Transactional
import com.networkedassets.git4c.core.bussiness.Database
import com.networkedassets.git4c.data.GlobForMacro

@Transactional
interface GlobForMacroDatabase : Database<GlobForMacro> {
    fun getByMacro(macroUuid: String): List<GlobForMacro>
}