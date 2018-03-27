package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.DeleteAllPredefinedGlobsCommand
import com.networkedassets.git4c.core.datastore.repositories.PredefinedGlobsDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class DeleteAllPredefinedGlobsUseCase(
        components: BussinesPluginComponents,
        val predefinedGlobsDatabase: PredefinedGlobsDatabase = components.database.predefinedGlobsDatabase
) : UseCase<DeleteAllPredefinedGlobsCommand, String>
(components) {

    override fun execute(request: DeleteAllPredefinedGlobsCommand): Result<String, Exception> {
        predefinedGlobsDatabase.removeAll()
        return Result.of { "" }
    }
}