package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v3

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
import com.atlassian.activeobjects.external.ModelVersion

class MacroSettingsUpgradeTask003 : ActiveObjectsUpgradeTask {

    override fun getModelVersion(): ModelVersion {
        return ModelVersion.valueOf("3")
    }

    override fun upgrade(currentVersion: ModelVersion, ao: ActiveObjects) {
    }
}