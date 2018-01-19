package com.networkedassets.git4c.infrastructure.mocks.infrastructure

import com.networkedassets.git4c.core.datastore.encryptors.RepositoryEncryptor
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.encryption.EncryptedRepository

class SimpleEncryptor: RepositoryEncryptor {
    override fun encrypt(decrypted: Repository) = EncryptedRepository(decrypted.uuid, decrypted, "")
    override fun decrypt(encrypted: EncryptedRepository) = encrypted.repository
}