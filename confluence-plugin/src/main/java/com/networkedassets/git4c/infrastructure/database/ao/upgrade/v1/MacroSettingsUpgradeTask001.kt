package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v1

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
import com.atlassian.activeobjects.external.ModelVersion

class MacroSettingsUpgradeTask001 : ActiveObjectsUpgradeTask {

    override fun getModelVersion(): ModelVersion {
        return ModelVersion.valueOf("1")
    }

    override fun upgrade(currentVersion: ModelVersion, ao: ActiveObjects) {
        ao.migrate(MacroSettingsEntity::class.java)

        for (macroSettings in ao.find(MacroSettingsEntity::class.java)) {
            macroSettings.defaultDocItem = "README.md"
            macroSettings.save()
        }
    }
}