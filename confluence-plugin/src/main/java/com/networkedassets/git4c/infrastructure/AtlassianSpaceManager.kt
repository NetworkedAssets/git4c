package com.networkedassets.git4c.infrastructure

import com.atlassian.confluence.spaces.SpaceStatus
import com.atlassian.sal.api.transaction.TransactionTemplate
import com.networkedassets.git4c.core.business.Space
import com.networkedassets.git4c.core.business.SpaceManager

typealias ConfluenceSpaceManager = com.atlassian.confluence.spaces.SpaceManager
typealias ConfluenceSpace = com.atlassian.confluence.spaces.Space

class AtlassianSpaceManager(
        val spaceManager: ConfluenceSpaceManager,
        private val transactionTemplate: TransactionTemplate
) : SpaceManager {
    override fun getAllSpaceKeys(): List<String> {

        return transactionTemplate.execute {
            spaceManager.getAllSpaceKeys(SpaceStatus.CURRENT).toList()
        }

    }

    override fun getSpace(spaceKey: String): Space? {
        return transactionTemplate.execute {
            val space = spaceManager.getSpace(spaceKey)

            if (space != null) {
                Space(space.id.toString(), space.key, space.name, space.urlPath)
            } else {
                null
            }
        }
    }

}