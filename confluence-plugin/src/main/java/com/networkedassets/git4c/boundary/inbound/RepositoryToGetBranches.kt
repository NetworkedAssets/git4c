package com.networkedassets.git4c.boundary.inbound

data class RepositoryToGetBranches(
        val sourceRepositoryUrl: String,
        val credentials: AuthorizationData
)