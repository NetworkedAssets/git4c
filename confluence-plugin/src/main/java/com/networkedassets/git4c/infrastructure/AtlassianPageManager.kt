package com.networkedassets.git4c.infrastructure

import com.atlassian.confluence.spaces.SpaceManager
import com.atlassian.confluence.spaces.SpaceStatus
import com.atlassian.sal.api.transaction.TransactionTemplate
import com.networkedassets.git4c.core.business.Page
import com.networkedassets.git4c.core.business.PageManager

typealias ConfluencePageManager = com.atlassian.confluence.pages.PageManager
typealias ConfluencePage = com.atlassian.confluence.pages.Page

class AtlassianPageManager(
        private val spaceManager: SpaceManager,
        private val pageManager: ConfluencePageManager,
        private val transactionTemplate: TransactionTemplate
) : PageManager {

    override fun getAllPageKeys(): List<Long> {
        val pages = transactionTemplate.execute {
            spaceManager.getAllSpaceKeys(SpaceStatus.CURRENT).flatMap {
                val space = spaceManager.getSpace(it)
                pageManager.getPageIds(space)
            }
        }
        return pages
    }

    override fun getAllPagesKeysForSpace(spaceKey: String): List<Long> {
        return transactionTemplate.execute {
            val space = spaceManager.getSpace(spaceKey) ?: return@execute listOf()
            pageManager.getPageIds(space).toList()
        }
    }

    override fun getPage(pageId: Long): Page? {
        return transactionTemplate.execute {
            val page = pageManager.getPage(pageId.toLong())
            if (page != null) {
                Page(page.idAsString, page.nameForComparison, page.urlPath, page.bodyAsString)
            } else {
                null
            }
        }
    }
}