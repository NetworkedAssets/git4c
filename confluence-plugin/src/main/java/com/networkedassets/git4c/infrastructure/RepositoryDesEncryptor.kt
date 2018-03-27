package com.networkedassets.git4c.infrastructure

import com.networkedassets.git4c.core.datastore.encryptors.RepositoryEncryptor
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword
import com.networkedassets.git4c.data.encryption.EncryptedRepository
import org.apache.commons.lang.RandomStringUtils


class RepositoryDesEncryptor : RepositoryEncryptor {

    val encryptor = SimpleCredentialsEncryptor()

    override fun encrypt(decrypted: Repository): EncryptedRepository {
        val key = RandomStringUtils.randomAscii(50)
        val repository = when (decrypted) {
            is RepositoryWithUsernameAndPassword -> {
                RepositoryWithUsernameAndPassword(
                        uuidOfRepository = decrypted.uuid,
                        path = decrypted.repositoryPath,
                        username = decrypted.username,
                        password = encryptor.encrypt(decrypted.password, key),
                        isEditable_ = decrypted.isEditable
                )
            }
            is RepositoryWithSshKey -> {
                RepositoryWithSshKey(
                        uuidOfRepository = decrypted.uuid,
                        path = decrypted.repositoryPath,
                        sshKey = encryptor.encrypt(decrypted.sshKey, key),
                        isEditable_ = decrypted.isEditable
                )
            }
            is RepositoryWithNoAuthorization -> {
                RepositoryWithNoAuthorization(
                        uuidOfRepository = decrypted.uuid,
                        path = decrypted.repositoryPath,
                        isEditable_ = decrypted.isEditable
                )
            }
            else -> {
                throw RuntimeException("All credential settings are null")
            }
        }
        return EncryptedRepository(decrypted.uuid, repository, key)
    }

    override fun decrypt(encrypted: EncryptedRepository): Repository {
        val repository = when (encrypted.repository) {
            is RepositoryWithUsernameAndPassword -> {
                RepositoryWithUsernameAndPassword(
                        uuidOfRepository = encrypted.repository.uuid,
                        path = encrypted.repository.repositoryPath,
                        username = encrypted.repository.username,
                        password = encryptor.decrypt(encrypted.repository.password, encrypted.securityKey),
                        isEditable_ = encrypted.repository.isEditable
                )
            }
            is RepositoryWithSshKey -> {
                RepositoryWithSshKey(
                        uuidOfRepository = encrypted.uuid,
                        path = encrypted.repository.repositoryPath,
                        sshKey = encryptor.decrypt(encrypted.repository.sshKey, encrypted.securityKey),
                        isEditable_ = encrypted.repository.isEditable
                )
            }
            is RepositoryWithNoAuthorization -> {
                RepositoryWithNoAuthorization(
                        uuidOfRepository = encrypted.uuid,
                        path = encrypted.repository.repositoryPath,
                        isEditable_ = encrypted.repository.isEditable
                )
            }
            else -> {
                throw RuntimeException("All credential settings are null")
            }
        }
        return repository;

    }
}