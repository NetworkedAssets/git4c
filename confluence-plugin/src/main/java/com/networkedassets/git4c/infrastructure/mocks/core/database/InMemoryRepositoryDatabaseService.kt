package com.networkedassets.git4c.infrastructure.mocks.core.database

import com.networkedassets.git4c.core.datastore.repositories.EncryptedRepositoryDatabase
import com.networkedassets.git4c.data.encryption.EncryptedRepository
import com.networkedassets.git4c.infrastructure.mocks.generic.InMemoryCache

class InMemoryRepositoryDatabaseService : InMemoryCache<EncryptedRepository>(), EncryptedRepositoryDatabase
