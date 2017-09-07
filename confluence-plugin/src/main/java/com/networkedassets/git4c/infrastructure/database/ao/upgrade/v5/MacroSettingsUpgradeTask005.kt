package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
import com.atlassian.activeobjects.external.ModelVersion
import com.networkedassets.git4c.infrastructure.UuidIdentifierGenerator
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.after.DocumentationsMacroSettingsAfter
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.after.NoAuthEntityAfter
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.after.SSHAuthEntityAfter
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.after.UsernamePasswordAuthEntityAfter
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.before.DocumentationsMacroSettingsBefore
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.before.NoAuthEntityBefore
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.before.SSHAuthEntityBefore
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.before.UsernamePasswordAuthEntityBefore

class MacroSettingsUpgradeTask005 : ActiveObjectsUpgradeTask {

    val identifierGenerator = UuidIdentifierGenerator()

    override fun getModelVersion(): ModelVersion {
        return ModelVersion.valueOf("5")
    }

    override fun upgrade(currentVersion: ModelVersion, ao: ActiveObjects) {
        ao.migrate(DocumentationsMacroSettingsBefore::class.java, NoAuthEntityBefore::class.java, SSHAuthEntityBefore::class.java, UsernamePasswordAuthEntityBefore::class.java)
        ao.migrate(DocumentationsMacroSettings::class.java, NoAuthEntity::class.java, SSHAuthEntity::class.java, UsernamePasswordAuthEntity::class.java)

        ao.find(DocumentationsMacroSettings::class.java).forEach { settings ->
            val repo = settings.auth

            if (repo != null) {
                val repouuid = identifierGenerator.generateNewIdentifier()
                repo.uuid = repouuid
                repo.securityKey = settings.securityKey
                repo.path = settings.path
                settings.repository = repouuid
                repo.save()
                settings.save()
            }
        }

        ao.migrate(DocumentationsMacroSettingsAfter::class.java, NoAuthEntityAfter::class.java, SSHAuthEntityAfter::class.java, UsernamePasswordAuthEntityAfter::class.java)
    }
}