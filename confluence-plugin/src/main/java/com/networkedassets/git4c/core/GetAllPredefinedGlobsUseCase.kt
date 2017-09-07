package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetAllPredefinedGlobsQuery
import com.networkedassets.git4c.boundary.outbound.PredefinedGlobData
import com.networkedassets.git4c.boundary.outbound.PredefinedGlobsData
import com.networkedassets.git4c.core.datastore.repositories.PredefinedGlobsDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetAllPredefinedGlobsUseCase(
        val predefinedGlobsDatabase: PredefinedGlobsDatabase
) : UseCase<GetAllPredefinedGlobsQuery, PredefinedGlobsData> {

    override fun execute(request: GetAllPredefinedGlobsQuery): Result<PredefinedGlobsData, Exception> {
        val globs = predefinedGlobsDatabase.getAll()
        return Result.of {
            PredefinedGlobsData(globs.map {
                PredefinedGlobData(it.uuid, it.name, it.glob)
            })
        }
    }
}