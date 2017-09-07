package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v8

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
import com.atlassian.activeobjects.external.ModelVersion

class PredefinedRepositoryUpgradeTask008 : ActiveObjectsUpgradeTask {

    override fun getModelVersion(): ModelVersion {
        return ModelVersion.valueOf("8")
    }

    override fun upgrade(currentVersion: ModelVersion, ao: ActiveObjects) {
        ao.migrate(PredefinedRepositoryEntityBefore::class.java)
        ao.migrate(PredefinedRepositoryEntityAfter::class.java)
        ao.migrate(RepositoryWithUsernameAndPasswordEntity::class.java)
        ao.migrate(RepositoryWithSshKeyEntity::class.java)
        ao.migrate(RepositoryWithNoAuthorizationEntity::class.java)

        val repositoryDatabase = ConfluenceActiveObjectRepository(ao)

        ao.find(PredefinedRepositoryEntityAfter::class.java).forEach {
            if (it.name == null) {
                it.name = repositoryDatabase.get(it.repository)?.repository?.repositoryPath ?: it.uuid
                it.save()
            }
        }
    }
}