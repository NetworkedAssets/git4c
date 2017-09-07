package com.networkedassets.git4c.infrastructure.database.ao.repository

import com.atlassian.activeobjects.external.ActiveObjects
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.data.PredefinedRepository
import com.networkedassets.git4c.infrastructure.database.ao.PredefinedRepositoryEntity
import net.java.ao.Query

class ConfluenceActiveObjectPredefinedRepository(val ao: ActiveObjects) : PredefinedRepositoryDatabase {

    override fun isAvailable(uuid: String): Boolean = getFromDatabase(uuid).isNotEmpty()

    override fun get(uuid: String) = getFromDatabase(uuid).firstOrNull()?.run { convert() }


    private fun getFromDatabase(uuid: String) = ao.find(PredefinedRepositoryEntity::class.java, Query.select().where("UUID = ?", uuid))

    override fun insert(uuid: String, data: PredefinedRepository) {
        val entity = ao.create(PredefinedRepositoryEntity::class.java)
        entity.uuid = uuid
        entity.repository = data.repositoryUuid
        entity.name = data.name
        entity.save()
    }

    override fun getAll(): List<PredefinedRepository> = ao.find(PredefinedRepositoryEntity::class.java).map { it.convert() }

    override fun update(uuid: String, data: PredefinedRepository) {
        remove(uuid)
        insert(uuid, data)
    }

    override fun remove(uuid: String) {
        getFromDatabase(uuid).firstOrNull()?.let { ao.delete(it) }
    }

    override fun removeAll() = ao.find(PredefinedRepositoryEntity::class.java).forEach { remove(it.uuid) }


    private fun PredefinedRepositoryEntity.convert(): PredefinedRepository {
        return PredefinedRepository(
                uuid = uuid,
                repositoryUuid = repository,
                name = name
        )
    }
}



