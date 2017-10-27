package com.networkedassets.git4c.infrastructure

import com.atlassian.sal.api.transaction.TransactionTemplate
import com.networkedassets.git4c.core.business.Space
import com.networkedassets.git4c.core.business.SpaceManager

typealias ConfluenceSpaceManager = com.atlassian.confluence.spaces.SpaceManager
typealias ConfluenceSpace = com.atlassian.confluence.spaces.Space

class AtlassianSpaceManager(
        val spaceManager: ConfluenceSpaceManager,
        private val transactionTemplate: TransactionTemplate
): SpaceManager {
    override fun getAllSpaces(): List<Space> {

        return transactionTemplate.execute {
            spaceManager.allSpaces
                    .map {
                        Space(it.id.toString(), it.name, it.urlPath)
                    }
        }

    }
}