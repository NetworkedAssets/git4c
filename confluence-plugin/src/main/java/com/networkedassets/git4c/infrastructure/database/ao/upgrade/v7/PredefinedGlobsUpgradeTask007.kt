package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v7

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
import com.atlassian.activeobjects.external.ModelVersion
import com.networkedassets.git4c.infrastructure.UuidIdentifierGenerator

class PredefinedGlobsUpgradeTask007 : ActiveObjectsUpgradeTask {

    val identifierGenerator = UuidIdentifierGenerator()

    val globsMap = listOf(
            "Gherkin" to "feature",
            "Kotlin" to "kt",
            "Scala" to "scala",
            "Java" to "java",
            "Markdown" to "md"
    ).map {
        val ext = it.second
        Pair(it.first, "**.$ext")
    }.toMap()


    override fun getModelVersion(): ModelVersion {
        return ModelVersion.valueOf("7")
    }

    override fun upgrade(currentVersion: ModelVersion, ao: ActiveObjects) {
        ao.migrate(PredefinedGlobEntity::class.java)
        globsMap.forEach({
            val name = it.key
            val glob = it.value
            val defaultGlob = ao.create(PredefinedGlobEntity::class.java)
            defaultGlob.uuid = identifierGenerator.generateNewIdentifier()
            defaultGlob.name = name
            defaultGlob.glob = glob
            defaultGlob.save()
        })
    }
}