package com.networkedassets.git4c.boundary.inbound


data class RepositoryToVerify(
        val sourceRepositoryUrl: String,
        val credentials: AuthorizationData
)
