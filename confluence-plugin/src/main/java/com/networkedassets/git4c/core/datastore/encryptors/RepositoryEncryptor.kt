package com.networkedassets.git4c.core.datastore.encryptors

import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.encryption.EncryptedRepository

interface RepositoryEncryptor : Encryptor<Repository, EncryptedRepository>