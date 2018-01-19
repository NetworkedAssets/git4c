package com.networkedassets.git4c.core.datastore.repositories

interface ThreadSettingsDatabase {

    fun setRevisionCheckThreadNumber(threadNumber: Int)
    fun getRevisionCheckThreadNumber(): Int

    fun setRepositoryExecutorThreadNumber(threadNumber: Int)
    fun getRepositoryExecutorThreadNumber(): Int

    fun setConverterExecutorThreadNumber(threadNumber: Int)
    fun getConverterExecutorThreadNumber(): Int

    fun setConfluenceQueryExecutorThreadNumber(threadNumber: Int)
    fun getConfluenceQueryExecutorThreadNumber(): Int

}

