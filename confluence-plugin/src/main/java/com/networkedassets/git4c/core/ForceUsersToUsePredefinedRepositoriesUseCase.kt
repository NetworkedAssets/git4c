package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.ForceUsersToUsePredefinedRepositoriesCommand
import com.networkedassets.git4c.boundary.outbound.isForcedPredefined
import com.networkedassets.git4c.core.datastore.repositories.PluginSettingsDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class ForceUsersToUsePredefinedRepositoriesUseCase(
        components: BussinesPluginComponents,
        val pluginSettings: PluginSettingsDatabase = components.database.pluginSettings
) : UseCase<ForceUsersToUsePredefinedRepositoriesCommand, isForcedPredefined>
(components) {
    override fun execute(request: ForceUsersToUsePredefinedRepositoriesCommand): Result<isForcedPredefined, Exception> {
        pluginSettings.setForcePredefinedRepositories(request.force.toForce)
        return Result.of { isForcedPredefined(request.force.toForce) }
    }


}