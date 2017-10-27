package com.networkedassets.git4c.core.datastore.repositories

import com.atlassian.activeobjects.tx.Transactional
import com.networkedassets.git4c.core.bussiness.Database
import com.networkedassets.git4c.core.datastore.extractors.EmptyExtractor
import com.networkedassets.git4c.core.datastore.extractors.ExtractorData

@Transactional
interface ExtractorDataDatabase : Database<ExtractorData> {
    fun getNullable(uuid: String?): ExtractorData? {
        return if (uuid == null) {
            EmptyExtractor()
        } else {
            get(uuid)
        }
    }
//    fun getByMacroNullable(macroUuid: String): ExtractorData?
//    fun getByMacro(macroUuid: String): ExtractorData {
//        return if (macroUuid.isEmpty()) {
//            EmptyExtractor()
//        } else {
//            getByMacroNullable(macroUuid) ?: EmptyExtractor()
//        }
//    }
}