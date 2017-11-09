package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.ForceUsersToUsePredefinedRepositoriesCommand
import com.networkedassets.git4c.boundary.outbound.isForcedPredefined
import com.networkedassets.git4c.core.datastore.PluginSettingsDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class ForceUsersToUsePredefinedRepositoriesUseCase(
        val pluginSettings: PluginSettingsDatabase
) : UseCase<ForceUsersToUsePredefinedRepositoriesCommand, isForcedPredefined> {
    override fun execute(request: ForceUsersToUsePredefinedRepositoriesCommand): Result<isForcedPredefined, Exception> {
        pluginSettings.setForcePredefinedRepositories(request.force.toForce)
        return Result.of { isForcedPredefined(request.force.toForce) }
    }


}