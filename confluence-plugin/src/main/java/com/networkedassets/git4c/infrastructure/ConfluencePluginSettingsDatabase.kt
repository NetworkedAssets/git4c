package com.networkedassets.git4c.infrastructure

import com.networkedassets.git4c.core.business.PluginSettings
import com.networkedassets.git4c.core.datastore.repositories.PluginSettingsDatabase


class ConfluencePluginSettingsDatabase(val pluginSettings: PluginSettings): PluginSettingsDatabase {

    val FILE_EDITING_KEY = "com.networkedassets.git4c.pluginsettings.fileediting"

    override fun setForcePredefinedRepositories(toForce: Boolean) {
        pluginSettings.remove("forcePredefinedRepositories")
        pluginSettings.put("forcePredefinedRepositories", getString(toForce))
    }

    override fun getForcePredefinedRepositoriesSetting(): Boolean? = pluginSettings.get("forcePredefinedRepositories") == "TRUE"

    private fun getString(force: Boolean): String {
        return if (force == true) "TRUE" else "FALSE"
    }

    override fun getFileEditingEnabled() = pluginSettings.get(FILE_EDITING_KEY)?.toBoolean() ?: false

    override fun setFileEditingEnabled(enabled: Boolean) {
        pluginSettings.put(FILE_EDITING_KEY, enabled.toString())
    }

}

