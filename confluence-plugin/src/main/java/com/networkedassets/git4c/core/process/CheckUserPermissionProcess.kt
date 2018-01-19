package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.core.common.PermissionChecker
import com.networkedassets.git4c.core.datastore.cache.PageAndSpacePermissionsForUserCache
import com.networkedassets.git4c.core.datastore.repositories.MacroLocationDatabase
import com.networkedassets.git4c.data.PageAndSpacePermissionsForUser
import com.networkedassets.git4c.utils.getLogger
import com.networkedassets.git4c.utils.info
import com.networkedassets.git4c.utils.warn

class CheckUserPermissionProcess(
        val macroLocationDatabase: MacroLocationDatabase,
        val permissionChecker: PermissionChecker,
        val pageAndSpacePermissionsForUserCache: PageAndSpacePermissionsForUserCache
) : ICheckUserPermissionProcess {

    private val log = getLogger()

    override fun userHasPermissionToMacro(macroId: String, user: String?): Boolean? {
        log.info { "Will check if user=${user} has permissions to the macro=${macroId}" }
        val macroLocation = macroLocationDatabase.get(macroId)

        if (macroLocation == null) {
            log.info { "Macro=${macroId} has a page unknown to plugin" }
            return null
        }

        log.info { "Macro=${macroId} has a space=${macroLocation.spaceKey} and page=${macroLocation.pageId}" }

        val existingPermission = pageAndSpacePermissionsForUserCache.get((user ?: "") + "_" + macroLocation.pageId + "_" + macroLocation.spaceKey)

        if (existingPermission != null) {
            return existingPermission.hasAccess
        }

        val hasAccess = permissionChecker.hasEnterPermission(user) &&
                permissionChecker.hasPagePermission(user, macroLocation.pageId) == true &&
                permissionChecker.hasSpacePermission(user, macroLocation.spaceKey) == true

        val newPermission = PageAndSpacePermissionsForUser(
                macroLocation.pageId,
                macroLocation.spaceKey,
                user ?: "",
                hasAccess
        )

        pageAndSpacePermissionsForUserCache.put(newPermission.uuid, newPermission)

        return newPermission.hasAccess
    }


}

interface ICheckUserPermissionProcess {
    fun userHasPermissionToMacro(macroId: String, user: String?): Boolean?
}