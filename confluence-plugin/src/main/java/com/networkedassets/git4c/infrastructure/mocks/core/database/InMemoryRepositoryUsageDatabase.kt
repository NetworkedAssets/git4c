package com.networkedassets.git4c.infrastructure.mocks.core.database

import com.networkedassets.git4c.core.datastore.repositories.RepositoryUsageDatabase
import com.networkedassets.git4c.data.RepositoryUsage
import com.networkedassets.git4c.infrastructure.mocks.generic.InMemoryCache

class InMemoryRepositoryUsageDatabase : InMemoryCache<RepositoryUsage>(), RepositoryUsageDatabase {
    override fun getByUsername(username: String): List<RepositoryUsage> {
        val list = super.map.filter { it.value.username == username }.values.toMutableList()
        list.sortByDescending { it.date }
        return list
    }

    override fun getByRepositoryUuid(repositoryUuid: String) = getAll().filter { it.repositoryUuid == repositoryUuid }

}