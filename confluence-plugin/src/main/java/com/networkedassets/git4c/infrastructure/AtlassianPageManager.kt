package com.networkedassets.git4c.infrastructure

import com.atlassian.confluence.spaces.SpaceManager
import com.atlassian.sal.api.transaction.TransactionTemplate
import com.networkedassets.git4c.core.business.Page
import com.networkedassets.git4c.core.business.PageManager

typealias ConfluencePageManager = com.atlassian.confluence.pages.PageManager
typealias ConfluencePage = com.atlassian.confluence.pages.Page

class AtlassianPageManager(
        private val spaceManager: SpaceManager,
        private val pageManager: ConfluencePageManager,
        private val transactionTemplate: TransactionTemplate
): PageManager {
    override fun getAllPages(): List<Page> {
        val spaces = transactionTemplate.execute { spaceManager.allSpaces.map { it.id } }

        val pages = spaces.flatMap {
            transactionTemplate.execute {
                pageManager.getPages(spaceManager.getSpace(it), true)
                        .map {
                            Page(it.idAsString, it.nameForComparison, it.urlPath, it.bodyAsString)
                        }
            }
        }

        return pages
    }

    override fun getAllPagesForSpace(spaceId: String): List<Page> {

        val space = transactionTemplate.execute { spaceManager.allSpaces.find { it.id.toString() == spaceId }?.id } ?: return listOf()

        return transactionTemplate.execute {
            pageManager.getPages(spaceManager.getSpace(space), true)
                    .map {
                        Page(it.idAsString, it.nameForComparison, it.urlPath, it.bodyAsString)
                    }
        }

    }

}