package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetPredefinedGlobByIdQuery
import com.networkedassets.git4c.boundary.outbound.PredefinedGlobData
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.repositories.PredefinedGlobsDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetPredefinedGlobByIdUseCase(
        val predefinedGlobsDatabase: PredefinedGlobsDatabase
) : UseCase<GetPredefinedGlobByIdQuery, PredefinedGlobData> {

    override fun execute(request: GetPredefinedGlobByIdQuery): Result<PredefinedGlobData, Exception> {
        val glob = predefinedGlobsDatabase.get(request.uuid) ?: return Result.error(NotFoundException(request.transactionInfo, ""))
        return Result.of {
            PredefinedGlobData(glob.uuid, glob.name, glob.glob)
        }
    }
}