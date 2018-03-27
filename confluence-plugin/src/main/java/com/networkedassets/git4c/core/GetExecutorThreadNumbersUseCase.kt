package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetExecutorThreadNumbersQuery
import com.networkedassets.git4c.boundary.outbound.ExecutorThreadNumbers
import com.networkedassets.git4c.core.business.ConfluenceQueryExecutorHolder
import com.networkedassets.git4c.core.business.ConverterExecutorHolder
import com.networkedassets.git4c.core.business.RepositoryPullExecutorHolder
import com.networkedassets.git4c.core.business.RevisionCheckExecutorHolder
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetExecutorThreadNumbersUseCase(
        components: BussinesPluginComponents,
        val revisionCheckExecutorHolder: RevisionCheckExecutorHolder = components.executors.revisionCheckExecutor,
        val repositoryPullExecutorHolder: RepositoryPullExecutorHolder = components.executors.repositoryPullExecutor,
        val converterExecutorHolder: ConverterExecutorHolder = components.executors.converterExecutor,
        val confluenceQueryExecutorHolder: ConfluenceQueryExecutorHolder = components.executors.confluenceQueryExecutor
) : UseCase<GetExecutorThreadNumbersQuery, ExecutorThreadNumbers>
(components) {
    override fun execute(request: GetExecutorThreadNumbersQuery): Result<ExecutorThreadNumbers, Exception> {
        return Result.of {
            ExecutorThreadNumbers(
                    revisionCheckExecutorHolder.getThreadNumber(),
                    repositoryPullExecutorHolder.getThreadNumber(),
                    converterExecutorHolder.getThreadNumber(),
                    confluenceQueryExecutorHolder.getThreadNumber()
            )
        }
    }

}