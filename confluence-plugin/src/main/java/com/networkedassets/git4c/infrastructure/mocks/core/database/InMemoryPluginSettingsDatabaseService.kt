package com.networkedassets.git4c.infrastructure.mocks.core.database

import com.networkedassets.git4c.core.datastore.repositories.PluginSettingsDatabase
import com.networkedassets.git4c.infrastructure.mocks.generic.InMemoryCache

class InMemoryPluginSettingsDatabaseService : InMemoryCache<String>(), PluginSettingsDatabase {

    override fun setForcePredefinedRepositories(toForce: Boolean) {
        this.remove("forcePredefinedRepositories")
        this.put("forcePredefinedRepositories", getString(toForce))
    }

    override fun getForcePredefinedRepositoriesSetting(): Boolean? = this.get("forcePredefinedRepositories") == "TRUE"

    private fun getString(force: Boolean): String {
        return if (force == true) "TRUE" else "FALSE"
    }

}
