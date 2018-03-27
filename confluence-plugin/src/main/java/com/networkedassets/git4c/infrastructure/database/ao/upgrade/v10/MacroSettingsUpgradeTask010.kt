package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v10

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
import com.atlassian.activeobjects.external.ModelVersion

class MacroSettingsUpgradeTask010: ActiveObjectsUpgradeTask {

    override fun upgrade(currentVersion: ModelVersion, ao: ActiveObjects) {
        ao.migrate(RepositoryWithNoAuthorizationEntityBefore::class.java, RepositoryWithSshKeyEntityBefore::class.java, RepositoryWithUsernameAndPasswordEntityBefore::class.java)
        ao.migrate(RepositoryWithNoAuthorizationEntityAfter::class.java, RepositoryWithSshKeyEntityAfter::class.java, RepositoryWithUsernameAndPasswordEntityAfter::class.java)

        ao.find(RepositoryWithSshKeyEntityAfter::class.java).forEach {
            it.editable = false
            it.save()
        }

        ao.find(RepositoryWithSshKeyEntityAfter::class.java).forEach {
            it.editable = false
            it.save()
        }

        ao.find(RepositoryWithUsernameAndPasswordEntityAfter::class.java).forEach {
            it.editable = false
            it.save()
        }

    }

    override fun getModelVersion(): ModelVersion {
        return ModelVersion.valueOf("10")
    }
}