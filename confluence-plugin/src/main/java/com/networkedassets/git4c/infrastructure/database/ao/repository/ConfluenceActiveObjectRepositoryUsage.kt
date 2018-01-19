package com.networkedassets.git4c.infrastructure.database.ao.repository

import com.atlassian.activeobjects.external.ActiveObjects
import com.networkedassets.git4c.core.datastore.repositories.RepositoryUsageDatabase
import com.networkedassets.git4c.data.RepositoryUsage
import com.networkedassets.git4c.infrastructure.database.ao.RepositoryUsageEntity
import com.networkedassets.git4c.utils.ActiveObjectsUtils.findByUuid
import net.java.ao.Query

class ConfluenceActiveObjectRepositoryUsage(val ao: ActiveObjects) : RepositoryUsageDatabase {

    override fun getByUsername(username: String): List<RepositoryUsage> {
        val list = ArrayList<RepositoryUsage>()
        list.addAll(ao.find(RepositoryUsageEntity::class.java, Query.select().where("USERNAME = ?", username)).map { it.convert() })
        list.sortByDescending { it.date }
        return list
    }

    override fun isAvailable(uuid: String) = getFromDatabase(uuid) != null

    override fun get(uuid: String) = getFromDatabase(uuid)?.run { convert() }

    override fun getAll(): List<RepositoryUsage> {
        val list = ArrayList<RepositoryUsage>()
        list.addAll(ao.find(RepositoryUsageEntity::class.java).map { it.convert() })
        return list
    }

    override fun put(uuid: String, data: RepositoryUsage) {
        val entity = ao.findByUuid(uuid) ?: ao.create(RepositoryUsageEntity::class.java)
        entity.uuid = uuid
        entity.date = data.date
        entity.repository = data.repositoryUuid
        entity.repositoryName = data.repositoryName
        entity.username = data.username

        entity.save()
    }

    override fun remove(uuid: String) {
        getFromDatabase(uuid)?.let { ao.delete(it) }
    }

    override fun removeAll() {
        ao.find(RepositoryUsageEntity::class.java).forEach { remove(it.uuid) }
    }

    private fun getFromDatabase(uuid: String): RepositoryUsageEntity? {
        val entity = ao.find(RepositoryUsageEntity::class.java, Query.select().where("UUID = ?", uuid)).firstOrNull()
        return entity
    }

    private fun RepositoryUsageEntity.convert(): RepositoryUsage {
        return RepositoryUsage(this.uuid, this.username, this.repository, this.repositoryName, this.date)
    }
}