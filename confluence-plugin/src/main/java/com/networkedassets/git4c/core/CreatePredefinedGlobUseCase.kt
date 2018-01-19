package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.CreatePredefinedGlobCommand
import com.networkedassets.git4c.boundary.outbound.PredefinedGlobData
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.core.datastore.repositories.PredefinedGlobsDatabase
import com.networkedassets.git4c.data.PredefinedGlob
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class CreatePredefinedGlobUseCase(
        val predefinedGlobsDatabase: PredefinedGlobsDatabase,
        val idGenerator: IdentifierGenerator
) : UseCase<CreatePredefinedGlobCommand, PredefinedGlobData> {

    override fun execute(request: CreatePredefinedGlobCommand): Result<PredefinedGlobData, Exception> {
        val globToCreate = PredefinedGlob(idGenerator.generateNewIdentifier(), request.globToCreate.glob, request.globToCreate.name)
        predefinedGlobsDatabase.put(globToCreate.uuid, globToCreate)
        return Result.of {
            PredefinedGlobData(
                    uuid = globToCreate.uuid,
                    name = globToCreate.name,
                    glob = globToCreate.glob
            )
        }
    }
}