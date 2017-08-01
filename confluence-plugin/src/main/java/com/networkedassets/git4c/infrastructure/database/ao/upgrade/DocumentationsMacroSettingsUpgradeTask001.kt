package com.networkedassets.git4c.infrastructure.database.ao.upgrade

import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ModelVersion



class DocumentationsMacroSettingsUpgradeTask001 : ActiveObjectsUpgradeTask {

    override fun getModelVersion(): ModelVersion {
        return ModelVersion.valueOf("1")
    }

    override fun upgrade(currentVersion: ModelVersion, ao: ActiveObjects)
    {
        ao.migrate(DocumentationsMacroSettingsEntity::class.java)

        for (macroSettings in ao.find(DocumentationsMacroSettingsEntity::class.java))
        {
            macroSettings.defaultDocItem = "README.md"
            macroSettings.save()
        }
    }
}