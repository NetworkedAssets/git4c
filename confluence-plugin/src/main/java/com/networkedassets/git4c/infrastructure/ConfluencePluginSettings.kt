package com.networkedassets.git4c.infrastructure

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
import com.networkedassets.git4c.core.datastore.PluginSettings


class ConfluencePluginSettings (val settingsFactory: PluginSettingsFactory): PluginSettings {
    val settings: com.atlassian.sal.api.pluginsettings.PluginSettings = settingsFactory.createGlobalSettings()

    override fun put(key: String, setting: String) {
        settings.put(key, setting)
    }

    override fun get(key: String): String? {
        return settings.get(key) as String?
    }

    override fun remove(key: String) {
        settings.remove(key)
    }

}