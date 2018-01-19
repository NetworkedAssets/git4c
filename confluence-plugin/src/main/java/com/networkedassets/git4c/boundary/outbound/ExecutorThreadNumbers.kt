package com.networkedassets.git4c.boundary.outbound

data class ExecutorThreadNumbers(
        val revisionCheckExecutor: Int,
        val repositoryPullExecutor: Int,
        val converterExecutor: Int,
        val confluenceQueryExecutor: Int
)