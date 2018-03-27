package com.networkedassets.git4c.infrastructure.database.ao.repository

import com.atlassian.activeobjects.external.ActiveObjects
import com.networkedassets.git4c.core.datastore.repositories.EncryptedRepositoryDatabase
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword
import com.networkedassets.git4c.data.encryption.EncryptedRepository
import com.networkedassets.git4c.infrastructure.database.ao.RepositoryEntity
import com.networkedassets.git4c.infrastructure.database.ao.RepositoryWithNoAuthorizationEntity
import com.networkedassets.git4c.infrastructure.database.ao.RepositoryWithSshKeyEntity
import com.networkedassets.git4c.infrastructure.database.ao.RepositoryWithUsernameAndPasswordEntity
import com.networkedassets.git4c.utils.ActiveObjectsUtils.findByUuid
import net.java.ao.Query

class ConfluenceActiveObjectRepository(val ao: ActiveObjects) : EncryptedRepositoryDatabase {

    override fun isAvailable(uuid: String): Boolean = getFromDatabase(uuid)?.let { true } ?: false

    override fun get(uuid: String) = getFromDatabase(uuid)?.run { convert() }

    private fun getFromDatabase(uuid: String): RepositoryEntity? {
        val noAuthorizationEntity = ao.find(RepositoryWithNoAuthorizationEntity::class.java, Query.select().where("UUID = ?", uuid)).firstOrNull()
        val withUsernameAndPasswordEntity = ao.find(RepositoryWithUsernameAndPasswordEntity::class.java, Query.select().where("UUID = ?", uuid)).firstOrNull()
        val withSshKeyEntity = ao.find(RepositoryWithSshKeyEntity::class.java, Query.select().where("UUID = ?", uuid)).firstOrNull()
        val result = noAuthorizationEntity ?: withUsernameAndPasswordEntity ?: withSshKeyEntity
        return result
    }

    override fun put(uuid: String, data: EncryptedRepository) {
        val entity = when (data.repository) {
            is RepositoryWithNoAuthorization -> {
                val no = ao.findByUuid(uuid) ?: ao.create(RepositoryWithNoAuthorizationEntity::class.java)
                no.path = data.repository.repositoryPath
                no.editable = data.repository.isEditable
                no
            }
            is RepositoryWithUsernameAndPassword -> {
                val uap = ao.findByUuid(uuid) ?: ao.create(RepositoryWithUsernameAndPasswordEntity::class.java)
                uap.username = data.repository.username
                uap.password = data.repository.password
                uap.editable = data.repository.isEditable
                uap.path = data.repository.repositoryPath
                uap
            }
            is RepositoryWithSshKey -> {
                val skc = ao.findByUuid(uuid) ?: ao.create(RepositoryWithSshKeyEntity::class.java)
                skc.key = data.repository.sshKey
                skc.editable = data.repository.isEditable
                skc.path = data.repository.repositoryPath
                skc
            }
            else -> throw RuntimeException("Unknown repositoryDetails type: ${data.repository.javaClass.name}")
        }
        entity.uuid = uuid
        entity.securityKey = data.securityKey
        entity.save()
    }

    override fun getAll(): List<EncryptedRepository> {
        val list = ArrayList<EncryptedRepository>()
        list.addAll(ao.find(RepositoryWithNoAuthorizationEntity::class.java).map { it.convert() })
        list.addAll(ao.find(RepositoryWithUsernameAndPasswordEntity::class.java).map { it.convert() })
        list.addAll(ao.find(RepositoryWithSshKeyEntity::class.java).map { it.convert() })
        return list
    }

    override fun remove(uuid: String) {
        getFromDatabase(uuid)?.let { ao.delete(it) }
    }

    override fun removeAll()  {
        ao.deleteWithSQL(RepositoryWithNoAuthorizationEntity::class.java, "ID > ?", 0)
        ao.deleteWithSQL(RepositoryWithUsernameAndPasswordEntity::class.java, "ID > ?", 0)
        ao.deleteWithSQL(RepositoryWithSshKeyEntity::class.java, "ID > ?", 0)
    }

    private fun RepositoryEntity.convert(): EncryptedRepository {
        val repository = when (this) {
            is RepositoryWithUsernameAndPasswordEntity -> {
                RepositoryWithUsernameAndPassword(
                        uuidOfRepository = uuid,
                        path = path,
                        isEditable_ = editable,
                        username = username,
                        password = password
                )
            }
            is RepositoryWithSshKeyEntity -> RepositoryWithSshKey(uuid, path, editable, key)
            is RepositoryWithNoAuthorizationEntity -> RepositoryWithNoAuthorization(uuid, path, editable)
            else -> throw RuntimeException("All credential settings are null")
        }
        return EncryptedRepository(uuid, repository, securityKey)
    }

}