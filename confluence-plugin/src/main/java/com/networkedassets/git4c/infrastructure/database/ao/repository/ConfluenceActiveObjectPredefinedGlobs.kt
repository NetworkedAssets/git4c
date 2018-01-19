package com.networkedassets.git4c.infrastructure.database.ao.repository

import com.atlassian.activeobjects.external.ActiveObjects
import com.networkedassets.git4c.core.datastore.repositories.PredefinedGlobsDatabase
import com.networkedassets.git4c.data.PredefinedGlob
import com.networkedassets.git4c.infrastructure.database.ao.PredefinedGlobEntity
import com.networkedassets.git4c.utils.ActiveObjectsUtils.findByUuid
import net.java.ao.Query

class ConfluenceActiveObjectPredefinedGlobs(val ao: ActiveObjects) : PredefinedGlobsDatabase {

    override fun isAvailable(uuid: String): Boolean = getFromDatabase(uuid).isNotEmpty()

    override fun get(uuid: String) = getFromDatabase(uuid).firstOrNull()?.run { convert() }

    private fun getFromDatabase(uuid: String) = ao.find(PredefinedGlobEntity::class.java, Query.select().where("UUID = ?", uuid))

    override fun put(uuid: String, data: PredefinedGlob) {
        val entity = ao.findByUuid(uuid) ?: ao.create(PredefinedGlobEntity::class.java)
        entity.uuid = uuid
        entity.glob = data.glob
        entity.name = data.name
        entity.save()
    }

    override fun getAll(): List<PredefinedGlob> = ao.find(PredefinedGlobEntity::class.java).map { it.convert() }

    override fun remove(uuid: String) {
        getFromDatabase(uuid).firstOrNull()?.let { ao.delete(it) }
    }

    override fun removeAll() = ao.find(PredefinedGlobEntity::class.java).forEach { remove(it.uuid) }


    private fun PredefinedGlobEntity.convert(): PredefinedGlob {
        return PredefinedGlob(
                uuid = uuid,
                glob = glob,
                name = name
        )
    }
}