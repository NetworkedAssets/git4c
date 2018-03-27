package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetPredefinedGlobByIdQuery
import com.networkedassets.git4c.boundary.outbound.PredefinedGlobData
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.repositories.PredefinedGlobsDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetPredefinedGlobByIdUseCase(
        components: BussinesPluginComponents,
        val predefinedGlobsDatabase: PredefinedGlobsDatabase = components.database.predefinedGlobsDatabase
) : UseCase<GetPredefinedGlobByIdQuery, PredefinedGlobData>
(components) {

    override fun execute(request: GetPredefinedGlobByIdQuery): Result<PredefinedGlobData, Exception> {
        val glob = predefinedGlobsDatabase.get(request.uuid)
                ?: return Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        return Result.of {
            PredefinedGlobData(glob.uuid, glob.name, glob.glob)
        }
    }
}