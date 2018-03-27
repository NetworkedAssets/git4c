package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.SaveExecutorThreadNumbersQuery
import com.networkedassets.git4c.core.business.ConfluenceQueryExecutorHolder
import com.networkedassets.git4c.core.business.ConverterExecutorHolder
import com.networkedassets.git4c.core.business.RepositoryPullExecutorHolder
import com.networkedassets.git4c.core.business.RevisionCheckExecutorHolder
import com.networkedassets.git4c.core.datastore.repositories.ThreadSettingsDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class SaveExecutorThreadNumbersUseCase(
        components: BussinesPluginComponents,
        val threadSettingsDatabase: ThreadSettingsDatabase = components.database.threadSettingsDatabase,
        val revisionCheckExecutorHolder: RevisionCheckExecutorHolder = components.executors.revisionCheckExecutor,
        val repositoryPullExecutorHolder: RepositoryPullExecutorHolder = components.executors.repositoryPullExecutor,
        val converterExecutorHolder: ConverterExecutorHolder = components.executors.converterExecutor,
        val confluenceQueryExecutorHolder: ConfluenceQueryExecutorHolder = components.executors.confluenceQueryExecutor
) : UseCase<SaveExecutorThreadNumbersQuery, Unit>
(components) {

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