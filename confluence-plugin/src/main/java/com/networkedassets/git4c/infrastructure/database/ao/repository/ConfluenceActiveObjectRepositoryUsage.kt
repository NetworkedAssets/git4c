package com.networkedassets.git4c.infrastructure.database.ao.repository

import com.atlassian.activeobjects.external.ActiveObjects
import com.networkedassets.git4c.core.datastore.repositories.RepositoryUsageDatabase
import com.networkedassets.git4c.data.RepositoryUsage
import com.networkedassets.git4c.infrastructure.database.ao.RepositoryUsageEntity
import com.networkedassets.git4c.utils.ActiveObjectsUtils.findByUuid
import net.java.ao.Query

class ConfluenceActiveObjectRepositoryUsage(val ao: ActiveObjects) : RepositoryUsageDatabase {

    override fun getByRepositoryUuid(repositoryUuid: String): List<RepositoryUsage> {
        return ao.find(RepositoryUsageEntity::class.java, Query.select().where("REPOSITORY = ?", repositoryUuid))
                .map { it.convert() }
                .sortedByDescending { it.date }
    }

    override fun getByUsername(username: String): List<RepositoryUsage> {
        return ao.find(RepositoryUsageEntity::class.java, Query.select().where("USERNAME = ?", username))
                .map { it.convert() }
                .sortedByDescending { it.date }
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

    override fun removeAll()  {
        ao.deleteWithSQL(RepositoryUsageEntity::class.java, "ID > ?", 0)
    }

    private fun getFromDatabase(uuid: String): RepositoryUsageEntity? {
        return ao.find(RepositoryUsageEntity::class.java, Query.select().where("UUID = ?", uuid)).firstOrNull()
    }

    private fun RepositoryUsageEntity.convert(): RepositoryUsage {
        return RepositoryUsage(this.uuid, this.username, this.repository, this.repositoryName, this.date)
    }
}