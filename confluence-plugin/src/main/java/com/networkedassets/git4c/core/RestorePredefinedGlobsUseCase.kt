package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.RestoreDefaultPredefinedGlobsCommand
import com.networkedassets.git4c.boundary.outbound.PredefinedGlobData
import com.networkedassets.git4c.boundary.outbound.PredefinedGlobsData
import com.networkedassets.git4c.core.business.DefaultGlobsMap
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.core.datastore.repositories.PredefinedGlobsDatabase
import com.networkedassets.git4c.data.PredefinedGlob
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class RestorePredefinedGlobsUseCase(
        val predefinedGlobsDatabase: PredefinedGlobsDatabase,
        val idGenerator: IdentifierGenerator
) : UseCase<RestoreDefaultPredefinedGlobsCommand, PredefinedGlobsData> {

    val globsMap = DefaultGlobsMap().defaultGlobs

    override fun execute(request: RestoreDefaultPredefinedGlobsCommand): Result<PredefinedGlobsData, Exception> {
        predefinedGlobsDatabase.removeAll()
        globsMap.forEach({
            val uuid = idGenerator.generateNewIdentifier()
            predefinedGlobsDatabase.put(uuid, PredefinedGlob(uuid, it.value, it.key))
        })
        val globs = predefinedGlobsDatabase.getAll()
        return Result.of {
            PredefinedGlobsData(globs.map {
                PredefinedGlobData(it.uuid, it.name, it.glob)
            })
        }
    }
}