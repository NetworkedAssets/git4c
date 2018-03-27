package com.networkedassets.git4c.core.datastore.repositories

interface PluginSettingsDatabase{
    fun setForcePredefinedRepositories(toForce: Boolean)
    fun getForcePredefinedRepositoriesSetting() : Boolean?
}
