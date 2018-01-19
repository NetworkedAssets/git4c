package com.networkedassets.git4c.infrastructure

import com.atlassian.sal.api.transaction.TransactionTemplate
import com.networkedassets.git4c.core.business.User
import com.networkedassets.git4c.core.business.UserManager

class AtlassianUserManager(
        private val userManager:  com.atlassian.user.UserManager,
        private val transactionTemplate: TransactionTemplate
): UserManager {
    override fun getUser(username: String) = transactionTemplate.execute {
        val confUser = userManager.getUser(username) ?: return@execute null
        User(confUser.fullName, confUser.email)
    }
}
