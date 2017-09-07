package com.networkedassets.git4c.boundary.inbound

data class RepositoryToGetFiles(
        val sourceRepositoryUrl: String,
        val credentials: AuthorizationData,
        val branch: String
)