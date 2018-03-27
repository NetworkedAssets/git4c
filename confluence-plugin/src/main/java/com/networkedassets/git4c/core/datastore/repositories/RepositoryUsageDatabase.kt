package com.networkedassets.git4c.core.datastore.repositories

import com.atlassian.activeobjects.tx.Transactional
import com.networkedassets.git4c.core.bussiness.Database
import com.networkedassets.git4c.data.RepositoryUsage

@Transactional
interface RepositoryUsageDatabase: Database<RepositoryUsage>{
    fun getByUsername(username: String): List<RepositoryUsage>
    fun getByRepositoryUuid(repositoryUuid: String): List<RepositoryUsage>
}