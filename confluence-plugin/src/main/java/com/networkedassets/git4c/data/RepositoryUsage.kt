package com.networkedassets.git4c.data

data class RepositoryUsage (
        val uuid: String,
        val username: String,
        val repositoryUuid: String,
        val repositoryName: String,
        val date: Long
)
