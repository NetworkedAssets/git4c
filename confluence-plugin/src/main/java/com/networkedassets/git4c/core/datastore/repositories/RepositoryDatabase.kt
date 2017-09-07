package com.networkedassets.git4c.core.datastore.repositories

import com.networkedassets.git4c.core.bussiness.Database
import com.networkedassets.git4c.core.datastore.encryptors.RepositoryEncryptor
import com.networkedassets.git4c.data.Repository

class RepositoryDatabase(
        val encryptor: RepositoryEncryptor,
        val repository: EncryptedRepositoryDatabase
) : Database<Repository> {

    override fun isAvailable(uuid: String): Boolean {
        return repository.isAvailable(uuid)
    }

    override fun get(uuid: String): Repository? {
        return encryptor.decrypt(repository.get(uuid) ?: return null)
    }

    override fun getAll(): List<Repository> {
        val list = repository.getAll()
        return list.map { encryptor.decrypt(it) }
    }

    override fun insert(uuid: String, data: Repository) {
        repository.insert(uuid, encryptor.encrypt(data))
    }

    override fun update(uuid: String, data: Repository) {
        repository.update(uuid, encryptor.encrypt(data))
    }

    override fun remove(uuid: String) {
        repository.remove(uuid)
    }

    override fun removeAll() {
        repository.removeAll()
    }
}