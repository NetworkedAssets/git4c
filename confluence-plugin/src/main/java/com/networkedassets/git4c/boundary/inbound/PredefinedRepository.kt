package com.networkedassets.git4c.boundary.inbound

data class PredefinedRepository(
        val sourceRepositoryUrl: String,
        val credentials: AuthorizationData,
        val repositoryName : String
)