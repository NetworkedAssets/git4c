package com.networkedassets.git4c.infrastructure.mocks.core.database

import com.networkedassets.git4c.core.datastore.repositories.ThreadSettingsDatabase
import com.networkedassets.git4c.infrastructure.mocks.generic.InMemoryCache

class InMemoryThreadSettingsDatabase : InMemoryCache<String>(), ThreadSettingsDatabase {

    val prefix = "com.networkedassets.git4c.thread.settings"

    val revisionCheckKey = "$prefix.revisionCheck"
    val repositoryPullExecutorKey = "$prefix.repositoryExecutor"
    val converterExecutorKey = "$prefix.converterExecutor"
    val confluenceQueryExecutorKey = "$prefix.confluenceQueryExecutor"

    override fun setRevisionCheckThreadNumber(threadNumber: Int) {
        put(revisionCheckKey, threadNumber.toString())
    }

    override fun getRevisionCheckThreadNumber(): Int {
        return get(revisionCheckKey)?.toInt() ?: 8
    }

    override fun setRepositoryExecutorThreadNumber(threadNumber: Int) {
        put(repositoryPullExecutorKey, threadNumber.toString())
    }

    override fun getRepositoryExecutorThreadNumber(): Int {
        return get(repositoryPullExecutorKey)?.toInt() ?: 2
    }

    override fun setConverterExecutorThreadNumber(threadNumber: Int) {
        put(converterExecutorKey, threadNumber.toString())
    }

    override fun getConverterExecutorThreadNumber(): Int {
        return get(converterExecutorKey)?.toInt() ?: 1
    }

    override fun setConfluenceQueryExecutorThreadNumber(threadNumber: Int) {
        put(confluenceQueryExecutorKey, threadNumber.toString())
    }

    override fun getConfluenceQueryExecutorThreadNumber(): Int {
        return get(confluenceQueryExecutorKey)?.toInt() ?: 2
    }

}
