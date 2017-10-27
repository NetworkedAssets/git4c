package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetSpacesWithMacroQuery
import com.networkedassets.git4c.boundary.outbound.MultiPageMacro
import com.networkedassets.git4c.boundary.outbound.Page
import com.networkedassets.git4c.boundary.outbound.SinglePageMacro
import com.networkedassets.git4c.boundary.outbound.Spaces
import com.networkedassets.git4c.core.business.*
import com.networkedassets.git4c.core.business.Macro.MacroType.MULTIFILE
import com.networkedassets.git4c.core.business.Macro.MacroType.SINGLEFILE
import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetSpacesWithMacroUseCase(
        private val spaceManager: SpaceManager,
        private val pageManager: PageManager,
        val macroSettingsDatabase: MacroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase,
        private val globForMacroDatabase: GlobForMacroDatabase,
        private val pageMacroExtractor: PageMacroExtractor
): UseCase<GetSpacesWithMacroQuery, Spaces> {

    override fun execute(request: GetSpacesWithMacroQuery): Result<Spaces, Exception> {

        val spaces = spaceManager.getAllSpaces()
                .asSequence()
                .mapNotNull {
                    val pages = getPagesForSpace(it)
                    if (pages.isNotEmpty()) {
                        com.networkedassets.git4c.boundary.outbound.Space(it.name, it.url, pages)
                    } else {
                        null
                    }
                }

        return Result.of { Spaces(spaces.toList()) }



//        spaceManager.getAllSpaces()
//                .asSequence()
//                .flatMap { space ->
//                    pageManager.getAllPagesForSpace(space.uuid)
//                            .map {
//                                Pair(space, it)
//                            }
//                            .asSequence()
//                }
//                .

//        val pages = pageManager.getAllPages()
//                .asSequence()
//                .mapNotNull { page ->
//
//                    val content = page.content
//
//                    val macros = pageMacroExtractor.extractMacro(content)
//                            .mapNotNull {
//                                val repositoryUuid = macroSettingsDatabase.get(it.uuid)?.repositoryUuid ?: ""
//                                val repository = repositoryDatabase.get(repositoryUuid)
//
//                                if (repository != null) {
//                                    val globs = globForMacroDatabase.getByMacro(it.uuid)
//                                    val glob = if(it.type == Macro.MacroType.SINGLEFILE) globs.getOrNull(0) else null
//
//                                    when(it.type) {
//                                        SINGLEFILE -> SinglePageMacro(repository.repositoryPath, glob?.glob ?: "")
//                                        MULTIFILE -> MultiPageMacro(repository.repositoryPath)
//                                    }
//                                } else {
//                                    null
//                                }
//                            }
//
//                    if (!macros.isEmpty()) {
//                        Page(page.name, page.url, macros)
//                    } else {
//                        null
//                    }
//                }
//
//        return Result.of { Spaces(pages.toList()) }
    }

    fun getPagesForSpace(space: Space): List<Page> {

        return pageManager.getAllPagesForSpace(space.uuid)
                .asSequence()
                .mapNotNull { page ->

                    val content = page.content

                    val macros = pageMacroExtractor.extractMacro(content)
                            .mapNotNull { (macroUuid, macroType) ->
                                val repositoryUuid = macroSettingsDatabase.get(macroUuid)?.repositoryUuid ?: ""
                                val repository = repositoryDatabase.get(repositoryUuid)

//                                if (repository != null) {
                                    val globs = globForMacroDatabase.getByMacro(macroUuid)
                                    val glob = if(macroType == Macro.MacroType.SINGLEFILE) globs.getOrNull(0) else null

                                    when(macroType) {
                                        SINGLEFILE -> SinglePageMacro(macroUuid, repository?.repositoryPath ?: "", glob?.glob ?: "")
                                        MULTIFILE -> MultiPageMacro(macroUuid, repository?.repositoryPath ?: "")
                                    }
//                                } else {
//                                    null
//                                }
                            }

                    if (!macros.isEmpty()) {
                        Page(page.name, page.url, macros)
                    } else {
                        null
                    }
                }
                .toList()

    }

}