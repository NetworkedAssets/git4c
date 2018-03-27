package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetAllPredefinedGlobsQuery
import com.networkedassets.git4c.boundary.outbound.PredefinedGlobData
import com.networkedassets.git4c.boundary.outbound.PredefinedGlobsData
import com.networkedassets.git4c.core.datastore.repositories.PredefinedGlobsDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetAllPredefinedGlobsUseCase(
        components: BussinesPluginComponents,
        val predefinedGlobsDatabase: PredefinedGlobsDatabase = components.database.predefinedGlobsDatabase
) : UseCase<GetAllPredefinedGlobsQuery, PredefinedGlobsData>
(components) {

    override fun execute(request: GetAllPredefinedGlobsQuery): Result<PredefinedGlobsData, Exception> {
        val globs = predefinedGlobsDatabase.getAll()
        return Result.of {
            PredefinedGlobsData(globs.map {
                PredefinedGlobData(it.uuid, it.name, it.glob)
            })
        }
    }
}