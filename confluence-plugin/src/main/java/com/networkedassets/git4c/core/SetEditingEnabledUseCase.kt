package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.SetEditingEnabledQuery
import com.networkedassets.git4c.core.datastore.repositories.PluginSettingsDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class SetEditingEnabledUseCase(
        val pluginSettings: PluginSettingsDatabase
): UseCase<SetEditingEnabledQuery, Unit> {

    override fun execute(request: SetEditingEnabledQuery): Result<Unit, Exception> {
        pluginSettings.setFileEditingEnabled(request.editingEnabled.editingEnabled)
        return Result.of {}
    }

}