package com.networkedassets.git4c.boundary.inbound

data class ExecutorThreadNumbersIn(
        val revisionCheckExecutor: Int,
        val repositoryPullExecutor: Int,
        val converterExecutor: Int,
        val confluenceQueryExecutor: Int
)