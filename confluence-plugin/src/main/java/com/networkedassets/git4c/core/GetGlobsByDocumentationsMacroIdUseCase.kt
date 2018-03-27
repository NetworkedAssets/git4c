package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetGlobsByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.GlobForMacro
import com.networkedassets.git4c.boundary.outbound.GlobsForMacro
import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.core.datastore.repositories.PredefinedGlobsDatabase
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetGlobsByDocumentationsMacroIdUseCase(
        components: BussinesPluginComponents,
        val globsForMacroDatabase: GlobForMacroDatabase = components.providers.globsForMacroProvider,
        val predefinedGlobsDatabase: PredefinedGlobsDatabase = components.database.predefinedGlobsDatabase,
        val checkUserPermissionProcess: ICheckUserPermissionProcess = components.processing.checkUserPermissionProcess
) : UseCase<GetGlobsByDocumentationsMacroIdQuery, GlobsForMacro>
(components) {

    override fun execute(request: GetGlobsByDocumentationsMacroIdQuery): Result<GlobsForMacro, Exception> {

        val macroId = request.macroId
        val user = request.user

        if (checkUserPermissionProcess.userHasPermissionToMacro(macroId, user) == false) {
            return Result.error(NotAuthorizedException("User doesn't have permission to this space"))
        }

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