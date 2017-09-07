package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetGlobsByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.GlobForMacro
import com.networkedassets.git4c.boundary.outbound.GlobsForMacro
import com.networkedassets.git4c.core.datastore.repositories.PredefinedGlobsDatabase
import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetGlobsByDocumentationsMacroIdUseCase(
        val globsForMacroDatabase: GlobForMacroDatabase,
        val predefinedGlobsDatabase: PredefinedGlobsDatabase
) : UseCase<GetGlobsByDocumentationsMacroIdQuery, GlobsForMacro> {

    override fun execute(request: GetGlobsByDocumentationsMacroIdQuery): Result<GlobsForMacro, Exception> {
        val macroId = request.macroId
        val globs = globsForMacroDatabase.getByMacro(macroId)
        val globsMap = predefinedGlobsDatabase.getAll()
        return Result.of {
            GlobsForMacro(globs.map {
                val glob = it.glob
                val prettyName = globsMap.firstOrNull { it.glob.contains(glob) }?.name ?: glob
                GlobForMacro(prettyName, glob)
            })
        }
    }
}