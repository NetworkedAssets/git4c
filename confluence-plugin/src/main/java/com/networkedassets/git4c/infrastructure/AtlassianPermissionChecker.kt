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
import org.slf4j.LoggerFactory

class AtlassianPermissionChecker(
        val permissionManager: PermissionManager,
        val pageManager: PageManager,
        val userManager: UserManager,
        val spaceManager: SpaceManager,
        private val transactionTemplate: TransactionTemplate
): PermissionChecker {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun hasPagePermission(username: String?, pageId: String): Boolean? {
        return transactionTemplate.execute {
            val page = pageManager.getAbstractPage(pageId.toLong())

            if (page == null) {
                log.warn("Cannot find page {}", pageId)
                null
            } else {
                permissionManager.hasPermission(usernameToUser(username), VIEW, page)
            }

        }
    }

    override fun hasSpacePermission(username: String?, spaceId: String): Boolean? {

        return transactionTemplate.execute {

            val space = spaceManager.getSpace(spaceId.toLong())

            if (space == null) {
                log.warn("Cannot find space {}", spaceId)
                null
            } else {
                permissionManager.hasPermission(usernameToUser(username), VIEW, space)
            }

        }
    }

    override fun hasEnterPermission(username: String?): Boolean {
        return transactionTemplate.execute {
            permissionManager.hasPermission(usernameToUser(username), VIEW, TARGET_APPLICATION)
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