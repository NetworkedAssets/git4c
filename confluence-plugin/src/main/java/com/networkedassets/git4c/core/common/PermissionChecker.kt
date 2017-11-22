package com.networkedassets.git4c.core.common

interface PermissionChecker {
    fun hasPagePermission(username: String?, pageId: String): Boolean?
    fun hasSpacePermission(username: String?, spaceId: String): Boolean?
    fun hasEnterPermission(username: String?): Boolean
}