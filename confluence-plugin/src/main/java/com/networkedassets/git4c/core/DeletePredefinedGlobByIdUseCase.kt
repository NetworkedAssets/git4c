package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.DeletePredefinedGlobByIdCommand
import com.networkedassets.git4c.core.datastore.repositories.PredefinedGlobsDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class DeletePredefinedGlobByIdUseCase(
        val predefinedGlobsDatabase: PredefinedGlobsDatabase
) : UseCase<DeletePredefinedGlobByIdCommand, String> {

    override fun execute(request: DeletePredefinedGlobByIdCommand): Result<String, Exception> {
        predefinedGlobsDatabase.remove(request.uuid)
        return Result.of { "" }
    }
}