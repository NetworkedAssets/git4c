package com.networkedassets.git4c.infrastructure

import com.atlassian.confluence.pages.PageManager
import com.atlassian.confluence.security.Permission.VIEW
import com.atlassian.confluence.security.PermissionManager
import com.atlassian.confluence.security.PermissionManager.TARGET_APPLICATION
import com.atlassian.confluence.spaces.SpaceManager
import com.atlassian.sal.api.transaction.TransactionTemplate
import com.atlassian.user.User
import com.atlassian.user.UserManager
import com.networkedassets.git4c.core.common.PermissionChecker
import com.networkedassets.git4c.utils.getLogger

class AtlassianPermissionChecker(
        val permissionManager: PermissionManager,
        val pageManager: PageManager,
        val userManager: UserManager,
        val spaceManager: SpaceManager,
        private val transactionTemplate: TransactionTemplate
) : PermissionChecker {

    private val log = getLogger()

    override fun hasPagePermission(username: String?, pageId: String): Boolean? {
        // TODO: MEMORY!!!
        // TODO: Make a cache for half an hour after first check
        return transactionTemplate.execute {
            val page = pageManager.getAbstractPage(pageId.toLong())
            if (page == null) {
                log.warn("Cannot find page ${pageId}")
                return@execute null
            } else {
                val answer = permissionManager.hasPermission(usernameToUser(username), VIEW, page)
                if (!answer) {
                    log.info("User ${username} has no permission to VIEW the page=${page.id}")
                }
                return@execute answer
            }
        }
    }

    override fun hasSpacePermission(username: String?, spaceId: String): Boolean? {
        // TODO: MEMORY!!!
        return transactionTemplate.execute {
            val space = spaceManager.getSpace(spaceId)
            if (space == null) {
                log.warn("Cannot find space ${spaceId}")
                return@execute null
            } else {
                val answer = permissionManager.hasPermission(usernameToUser(username), VIEW, space)
                if (!answer) {
                    log.info("User ${username} has no permission to VIEW the space=${space.id}")
                }
                return@execute answer
            }
        }
    }

    override fun hasEnterPermission(username: String?): Boolean {
        return transactionTemplate.execute {
            val answer = permissionManager.hasPermission(usernameToUser(username), VIEW, TARGET_APPLICATION)
            if (!answer) {
                log.info("User ${username} has no permission to VIEW the target application")
            }
            return@execute answer
        }
    }

    private fun usernameToUser(username: String?): User? {
        if (username == null) {
            return null
        } else {
            return userManager.getUser(username)
        }
    }

}