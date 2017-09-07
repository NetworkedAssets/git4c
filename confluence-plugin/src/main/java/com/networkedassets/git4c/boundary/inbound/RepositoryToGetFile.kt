package com.networkedassets.git4c.boundary.inbound

data class RepositoryToGetFile(
        val sourceRepositoryUrl: String,
        val credentials: AuthorizationData,
        val branch: String,
        val file: String
)