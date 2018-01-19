package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetExecutorThreadNumbersQuery
import com.networkedassets.git4c.boundary.outbound.ExecutorThreadNumbers
import com.networkedassets.git4c.core.business.ConfluenceQueryExecutorHolder
import com.networkedassets.git4c.core.business.ConverterExecutorHolder
import com.networkedassets.git4c.core.business.RepositoryPullExecutorHolder
import com.networkedassets.git4c.core.business.RevisionCheckExecutorHolder
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetExecutorThreadNumbersUseCase(
        val revisionCheckExecutorHolder: RevisionCheckExecutorHolder,
        val repositoryPullExecutorHolder: RepositoryPullExecutorHolder,
        val converterExecutorHolder: ConverterExecutorHolder,
        val confluenceQueryExecutorHolder: ConfluenceQueryExecutorHolder
) : UseCase<GetExecutorThreadNumbersQuery, ExecutorThreadNumbers> {
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