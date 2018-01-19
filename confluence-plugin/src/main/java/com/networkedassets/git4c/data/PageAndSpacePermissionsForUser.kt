package com.networkedassets.git4c.data

data class PageAndSpacePermissionsForUser(
        val pageId: String,
        val spaceKey: String,
        val username: String,
        val hasAccess: Boolean
) {
    val uuid = username + "_" + pageId + "_" + spaceKey
}