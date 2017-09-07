package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v2

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
import com.atlassian.activeobjects.external.ModelVersion

class MacroSettingsUpgradeTask002 : ActiveObjectsUpgradeTask {

    override fun getModelVersion(): ModelVersion {
        return ModelVersion.valueOf("2")
    }

    override fun upgrade(currentVersion: ModelVersion, ao: ActiveObjects) {
        ao.migrate(DocumentationsMacroSettingsEntity::class.java, GlobEntity::class.java)

        for (macroSettings in ao.find(DocumentationsMacroSettingsEntity::class.java)) {
            val glob: String? = macroSettings.glob
            if (!glob.isNullOrEmpty()) {
                val globEntity = ao.create(GlobEntity::class.java)
                globEntity.glob = glob!!
                globEntity.macroSettings = macroSettings
                globEntity.save()
            }
        }
    }
}