package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.SaveExecutorThreadNumbersQuery
import com.networkedassets.git4c.core.business.ConfluenceQueryExecutorHolder
import com.networkedassets.git4c.core.business.ConverterExecutorHolder
import com.networkedassets.git4c.core.business.RepositoryPullExecutorHolder
import com.networkedassets.git4c.core.business.RevisionCheckExecutorHolder
import com.networkedassets.git4c.core.datastore.repositories.ThreadSettingsDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class SaveExecutorThreadNumbersUseCase(
        val threadSettingsDatabase: ThreadSettingsDatabase,
        val revisionCheckExecutorHolder: RevisionCheckExecutorHolder,
        val repositoryPullExecutorHolder: RepositoryPullExecutorHolder,
        val converterExecutorHolder: ConverterExecutorHolder,
        val confluenceQueryExecutorHolder: ConfluenceQueryExecutorHolder
) : UseCase<SaveExecutorThreadNumbersQuery, Unit> {
    override fun execute(request: SaveExecutorThreadNumbersQuery): Result<Unit, Exception> {
        threadSettingsDatabase.setConverterExecutorThreadNumber(request.numbers.converterExecutor)
        threadSettingsDatabase.setRepositoryExecutorThreadNumber(request.numbers.repositoryPullExecutor)
        threadSettingsDatabase.setRevisionCheckThreadNumber(request.numbers.revisionCheckExecutor)
        threadSettingsDatabase.setConfluenceQueryExecutorThreadNumber(request.numbers.confluenceQueryExecutor)

        revisionCheckExecutorHolder.reset(request.numbers.revisionCheckExecutor)
        repositoryPullExecutorHolder.reset(request.numbers.repositoryPullExecutor)
        converterExecutorHolder.reset(request.numbers.converterExecutor)
        confluenceQueryExecutorHolder.reset(request.numbers.confluenceQueryExecutor)
        return Result.of { Unit }
    }

}