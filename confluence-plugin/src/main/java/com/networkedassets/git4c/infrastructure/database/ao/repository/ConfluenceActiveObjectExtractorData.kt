package com.networkedassets.git4c.infrastructure.database.ao.repository

import com.atlassian.activeobjects.external.ActiveObjects
import com.networkedassets.git4c.core.datastore.extractors.ExtractorData
import com.networkedassets.git4c.core.datastore.repositories.ExtractorDataDatabase
import com.networkedassets.git4c.core.datastore.extractors.LineNumbersExtractorData
import com.networkedassets.git4c.core.datastore.extractors.MethodExtractorData
import com.networkedassets.git4c.infrastructure.database.ao.ExtractorEntity
import com.networkedassets.git4c.infrastructure.database.ao.ExtractorLineNumbersEntity
import com.networkedassets.git4c.infrastructure.database.ao.ExtractorMethodEntity
import com.networkedassets.git4c.utils.ActiveObjectsUtils.findByUuid

class ConfluenceActiveObjectExtractorData(val ao: ActiveObjects) : ExtractorDataDatabase {

    override fun isAvailable(uuid: String) = getFromDatabase(uuid) != null

    override fun get(uuid: String) = convertFromDatabase(getFromDatabase(uuid))

    override fun getAll() = getAllFromDatabase().mapNotNull { convertFromDatabase(it) }

    override fun insert(uuid: String, data: ExtractorData) {
        convertToDatabase(uuid, data)
    }

    override fun update(uuid: String, data: ExtractorData) {
        remove(uuid)
        insert(uuid, data)
    }

    override fun remove(uuid: String) {
        val extractor = getFromDatabase(uuid)
        if (extractor != null) {
            ao.delete(extractor)
        }
    }

    override fun removeAll() = getAllFromDatabase().forEach { ao.delete(it) }


    private fun getFromDatabase(uuid: String): ExtractorEntity? {
        val lines: ExtractorLineNumbersEntity? = ao.findByUuid(uuid)
        val method: ExtractorMethodEntity? = ao.findByUuid(uuid)
        return lines ?: method
    }

    private fun getAllFromDatabase(): List<ExtractorEntity> {
        val lines = ao.find(ExtractorLineNumbersEntity::class.java).toList()
        val method = ao.find(ExtractorMethodEntity::class.java).toList()
        return lines + method
    }

    private fun convertFromDatabase(extractor: ExtractorEntity?): ExtractorData? {
        if (extractor == null) {
            return null
        }

        return when (extractor) {
            is ExtractorLineNumbersEntity -> LineNumbersExtractorData(extractor.uuid, extractor.startLine, extractor.endLine)
            is ExtractorMethodEntity -> MethodExtractorData(extractor.uuid, extractor.method)
            else -> throw RuntimeException("Unknown extractor entity type: ${extractor::class.java}")
        }
    }

    private fun convertToDatabase(uuid: String, data: ExtractorData): ExtractorEntity {

        val extractor: ExtractorEntity

        when(data) {
            is LineNumbersExtractorData -> {
                val entity = ao.create(ExtractorLineNumbersEntity::class.java)
                entity.startLine = data.startLine
                entity.endLine = data.endLine
                extractor = entity
            }
            is MethodExtractorData -> {
                val entity = ao.create(ExtractorMethodEntity::class.java)
                entity.method = data.method
                extractor = entity
            }
            else -> {
                throw RuntimeException("Unknown extractor type: ${data::class.java}")
            }
        }
        extractor.uuid = uuid
        extractor.save()
        return extractor
    }

}

