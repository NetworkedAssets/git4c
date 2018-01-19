package com.networkedassets.git4c.core.datastore.repositories

import com.atlassian.activeobjects.tx.Transactional
import com.networkedassets.git4c.core.bussiness.Database
import com.networkedassets.git4c.core.datastore.extractors.EmptyExtractorData
import com.networkedassets.git4c.core.datastore.extractors.ExtractorData

@Transactional
interface ExtractorDataDatabase : Database<ExtractorData> {
    fun getNullable(uuid: String?): ExtractorData? {
        return if (uuid == null) {
            EmptyExtractorData
        } else {
            get(uuid)
        }
    }
}