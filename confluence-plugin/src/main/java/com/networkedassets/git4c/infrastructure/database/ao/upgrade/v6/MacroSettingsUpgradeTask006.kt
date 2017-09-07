package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v6

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
import com.atlassian.activeobjects.external.ModelVersion

/**
 * To be explicit about every change
 */
class MacroSettingsUpgradeTask006 : ActiveObjectsUpgradeTask {

    override fun getModelVersion(): ModelVersion {
        return ModelVersion.valueOf("6")
    }

    override fun upgrade(currentVersion: ModelVersion, ao: ActiveObjects) {
        ao.migrate(MacroSettingsEntityBefore::class.java)
        ao.migrate(MacroSettingsEntityAfter::class.java)
    }
}