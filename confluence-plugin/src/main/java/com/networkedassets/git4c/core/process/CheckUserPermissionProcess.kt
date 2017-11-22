package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.core.business.PageMacroExtractor
import com.networkedassets.git4c.core.business.PageManager
import com.networkedassets.git4c.core.business.SpaceManager
import com.networkedassets.git4c.core.common.PermissionChecker
import com.networkedassets.git4c.core.datastore.MacroIdToSpaceAndPageDatabase

open class CheckUserPermissionProcess(
        val spaceManager: SpaceManager,
        val pageManager: PageManager,
        val macroExtractor: PageMacroExtractor,
        val macroIdToSpaceAndPageDatabase: MacroIdToSpaceAndPageDatabase,
        val permissionChecker: PermissionChecker
): ICheckUserPermissionProcess {

    val lock = java.lang.Object()

    /**
     * Null returned when macro doesn't exist
     */
    override fun userHasPermissionToMacro(macroId: String, user: String?): Boolean? = synchronized(lock) {

        val pair1 = macroIdToSpaceAndPageDatabase.getSpaceAndPageForMacro(macroId)

        if (pair1 == null) {

            val macrosToSpacesAndPages = spaceManager.getAllSpaces()
                    .map {
                        it to pageManager.getAllPagesForSpace(it.uuid)
                    }
                    .flatMap {
                        val pair = it
                        it.second.flatMap { page ->
                            val macros = macroExtractor.extractMacro(page.content)

                            macros.map {
                              it.uuid to Pair(pair.first.uuid, page.id)
                            }

                        }
                    }
                    .toMap()

            macroIdToSpaceAndPageDatabase.clear()

            macrosToSpacesAndPages.forEach { macro, pair ->
                macroIdToSpaceAndPageDatabase.putStaceAndPageForMacro(macro, pair)
            }


        }

        //We've tried second time and nothing was found - macro doesn't exist anymore
        val pair = pair1 ?: macroIdToSpaceAndPageDatabase.getSpaceAndPageForMacro(macroId) ?: return null

        val (spaceId, pageId) = pair

        return permissionChecker.hasEnterPermission(user) &&
                permissionChecker.hasPagePermission(user, pageId) == true &&
                permissionChecker.hasSpacePermission(user, spaceId) == true

    }

}

interface ICheckUserPermissionProcess {
    fun userHasPermissionToMacro(macroId: String, user: String?): Boolean?
}