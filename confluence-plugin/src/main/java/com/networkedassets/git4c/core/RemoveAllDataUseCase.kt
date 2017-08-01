package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.RemoveAllDataCommand
import com.networkedassets.git4c.core.datastore.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.MacroSettingsRepository
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class RemoveAllDataUseCase(
        val macroSettingsRepository: MacroSettingsRepository,
        val documentsViewCache: DocumentsViewCache
) : UseCase<RemoveAllDataCommand, Unit> {
    override fun execute(request: RemoveAllDataCommand) = Result.of {
        macroSettingsRepository.removeAll()
        documentsViewCache.removeAll()
    }
}
