package com.networkedassets.git4c.core.datastore

interface PluginSettingsDatabase{

    fun setForcePredefinedRepositories(toForce: Boolean)
    fun getForcePredefinedRepositoriesSetting() : Boolean?

}

