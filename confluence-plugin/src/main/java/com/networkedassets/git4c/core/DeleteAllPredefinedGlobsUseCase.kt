package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.DeleteAllPredefinedGlobsCommand
import com.networkedassets.git4c.core.datastore.repositories.PredefinedGlobsDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class DeleteAllPredefinedGlobsUseCase(
        val predefinedGlobsDatabase: PredefinedGlobsDatabase
) : UseCase<DeleteAllPredefinedGlobsCommand, String> {

    override fun execute(request: DeleteAllPredefinedGlobsCommand): Result<String, Exception> {
        predefinedGlobsDatabase.removeAll()
        return Result.of { "" }
    }
}