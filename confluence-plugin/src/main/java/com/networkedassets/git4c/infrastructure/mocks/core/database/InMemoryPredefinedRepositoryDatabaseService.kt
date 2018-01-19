package com.networkedassets.git4c.infrastructure.mocks.core.database

import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.data.PredefinedRepository
import com.networkedassets.git4c.infrastructure.mocks.generic.InMemoryCache

class InMemoryPredefinedRepositoryDatabaseService : PredefinedRepositoryDatabase, InMemoryCache<PredefinedRepository>()
