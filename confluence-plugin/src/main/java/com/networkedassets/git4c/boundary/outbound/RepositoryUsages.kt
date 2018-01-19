package com.networkedassets.git4c.boundary.outbound

data class RepositoryUsages(val usages: List<RepositoryUsage>)

data class RepositoryUsage(val repositoryName: String, val uuid: String)