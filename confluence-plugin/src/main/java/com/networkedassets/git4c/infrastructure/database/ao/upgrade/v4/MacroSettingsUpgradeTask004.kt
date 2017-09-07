package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v4

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
import com.atlassian.activeobjects.external.ModelVersion
import com.networkedassets.git4c.infrastructure.UuidIdentifierGenerator

class MacroSettingsUpgradeTask004 : ActiveObjectsUpgradeTask {

    val identifierGenerator = UuidIdentifierGenerator()

    override fun getModelVersion(): ModelVersion {
        return ModelVersion.valueOf("4")
    }

    override fun upgrade(currentVersion: ModelVersion, ao: ActiveObjects) {

        ao.migrate(DocumentationsMacroSettingsBefore::class.java, GlobEntityBefore::class.java)
        ao.migrate(GlobEntity::class.java)

        ao.find(GlobEntity::class.java)
                .filter { it.macroSettings != null }
                .groupBy { it.macroSettings.uuid.toString() }
                .forEach { macroSettingsUUid, globs ->
                    println(globs.map { it.glob })
                    globs
                            .distinctBy { it.glob.toString() }
                            .forEach { glob ->
                                glob.macro = macroSettingsUUid
                                glob.uuid = identifierGenerator.generateNewIdentifier()
                                glob.save()
                            }

                }

        ao.delete(*ao.find(GlobEntity::class.java).filter { it.uuid.isNullOrEmpty() }.toTypedArray())
        ao.migrate(GlobEntityAfter::class.java)
    }
}