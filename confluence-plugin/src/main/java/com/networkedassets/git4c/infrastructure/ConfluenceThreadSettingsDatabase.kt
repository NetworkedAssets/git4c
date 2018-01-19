package com.networkedassets.git4c.infrastructure

import com.networkedassets.git4c.core.business.PluginSettings
import com.networkedassets.git4c.core.datastore.repositories.ThreadSettingsDatabase

class ConfluenceThreadSettingsDatabase(val pluginSettings: PluginSettings): ThreadSettingsDatabase {

    val prefix = "com.networkedassets.git4c.thread.settings"

    val revisionCheckKey = "$prefix.revisionCheck"
    val repositoryPullExecutorKey = "$prefix.repositoryExecutor"
    val converterExecutorKey = "$prefix.converterExecutor"
    val confluenceQueryExecutorKey = "$prefix.confluenceQueryExecutor"

    override fun setRevisionCheckThreadNumber(threadNumber: Int) {
        pluginSettings.put(revisionCheckKey, threadNumber.toString())
    }

    override fun getRevisionCheckThreadNumber(): Int {
        return pluginSettings.get(revisionCheckKey)?.toInt() ?: 8
    }

    override fun setRepositoryExecutorThreadNumber(threadNumber: Int) {
        pluginSettings.put(repositoryPullExecutorKey, threadNumber.toString())
    }

    override fun getRepositoryExecutorThreadNumber(): Int {
        return pluginSettings.get(repositoryPullExecutorKey)?.toInt() ?: 2
    }

    override fun setConverterExecutorThreadNumber(threadNumber: Int) {
        pluginSettings.put(converterExecutorKey, threadNumber.toString())
    }

    override fun getConverterExecutorThreadNumber(): Int {
        return pluginSettings.get(converterExecutorKey)?.toInt() ?: 1
    }

    override fun setConfluenceQueryExecutorThreadNumber(threadNumber: Int) {
        pluginSettings.put(confluenceQueryExecutorKey, threadNumber.toString())
    }

    override fun getConfluenceQueryExecutorThreadNumber(): Int {
        return pluginSettings.get(confluenceQueryExecutorKey)?.toInt() ?: 2
    }

}