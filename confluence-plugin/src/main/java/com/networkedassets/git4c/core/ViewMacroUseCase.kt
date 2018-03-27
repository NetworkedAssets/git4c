package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.ViewMacroCommand
import com.networkedassets.git4c.boundary.outbound.MacroView
import com.networkedassets.git4c.core.datastore.repositories.MacroLocationDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.process.MacroViewProcess
import com.networkedassets.git4c.data.MacroLocation
import com.networkedassets.git4c.data.MacroType
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class ViewMacroUseCase(
        components: BussinesPluginComponents,
        val macroLocationDatabase: MacroLocationDatabase = components.database.macroLocationDatabase,
        val macroViewProcess: MacroViewProcess = components.processing.macroViewProcess,
        val macroSettingsDatabase: MacroSettingsDatabase = components.providers.macroSettingsProvider
) : UseCase<ViewMacroCommand, MacroView>
(components) {

    override fun execute(request: ViewMacroCommand): Result<MacroView, Exception> {

        updateMacroLocation(request)

        updateMacroType(request)

        macroViewProcess.prepareMacroToBeViewed(request.macro.uuid);

        return Result.of { MacroView(request.macro.uuid, request.page.id, request.space.uuid) }
    }

    private fun updateMacroType(request: ViewMacroCommand) {
        macroSettingsDatabase.get(request.macro.uuid)?.apply {
            this.type = MacroType.valueOf(request.macro.type.name)
            macroSettingsDatabase.put(request.macro.uuid, this)
        }
    }

    private fun updateMacroLocation(request: ViewMacroCommand) {
        macroLocationDatabase.put(request.macro.uuid,
                MacroLocation(
                        request.macro.uuid,
                        request.page.id,
                        request.space.uuid
                ))
    }
}