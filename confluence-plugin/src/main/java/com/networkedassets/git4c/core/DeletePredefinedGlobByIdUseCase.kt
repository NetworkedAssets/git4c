package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.DeletePredefinedGlobByIdCommand
import com.networkedassets.git4c.core.datastore.repositories.PredefinedGlobsDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class DeletePredefinedGlobByIdUseCase(
        components: BussinesPluginComponents,
        val predefinedGlobsDatabase: PredefinedGlobsDatabase = components.database.predefinedGlobsDatabase
) : UseCase<DeletePredefinedGlobByIdCommand, String>
(components) {

    override fun execute(request: DeletePredefinedGlobByIdCommand): Result<String, Exception> {
        predefinedGlobsDatabase.remove(request.uuid)
        return Result.of { "" }
    }
}