package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v9

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
import com.atlassian.activeobjects.external.ModelVersion
import com.networkedassets.git4c.infrastructure.UuidIdentifierGenerator

class MacroSettingsUpgradeTask009 : ActiveObjectsUpgradeTask {

    private val identifierGenerator = UuidIdentifierGenerator()

    override fun getModelVersion(): ModelVersion {
        return ModelVersion.valueOf("9")
    }

    override fun upgrade(currentVersion: ModelVersion, ao: ActiveObjects) {

        ao.migrate(ExtractorMethodEntity::class.java, ExtractorLineNumbersEntity::class.java)
        ao.migrate(MacroSettingsEntityBefore::class.java)
        ao.migrate(MacroSettingsEntity::class.java)

        ao.find(MacroSettingsEntity::class.java).forEach {
            if (!it.method.isNullOrEmpty()) {
                val entity = ao.create(ExtractorMethodEntity::class.java)
                val uuid = identifierGenerator.generateNewIdentifier()
                entity.uuid = uuid
                entity.method = it.method
                entity.save()
                it.extractor = uuid
                it.save()
            }
        }

        ao.migrate(MacroSettingsEntityAfter::class.java)

    }
}