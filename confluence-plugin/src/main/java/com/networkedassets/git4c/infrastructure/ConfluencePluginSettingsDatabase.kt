package com.networkedassets.git4c.infrastructure

import com.networkedassets.git4c.core.datastore.PluginSettings
import com.networkedassets.git4c.core.datastore.PluginSettingsDatabase


class ConfluencePluginSettingsDatabase(val pluginSettings: PluginSettings): PluginSettingsDatabase {

    override fun setForcePredefinedRepositories(toForce: Boolean) {
        pluginSettings.remove("forcePredefinedRepositories")
        pluginSettings.put("forcePredefinedRepositories", getString(toForce))
    }

    override fun getForcePredefinedRepositoriesSetting(): Boolean? = pluginSettings.get("forcePredefinedRepositories") == "TRUE"

    private fun getString(force: Boolean): String {
        return if (force == true) "TRUE" else "FALSE"
    }
}

