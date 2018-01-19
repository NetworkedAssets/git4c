package com.networkedassets.git4c.infrastructure.database.ao.repository

import com.atlassian.activeobjects.external.ActiveObjects
import com.networkedassets.git4c.core.datastore.repositories.MacroLocationDatabase
import com.networkedassets.git4c.data.MacroLocation
import com.networkedassets.git4c.infrastructure.database.ao.MacroLocationEntity
import com.networkedassets.git4c.utils.ActiveObjectsUtils.findByUuid
import net.java.ao.Query

class ConfluenceActiveObjectMacroLocation(val ao: ActiveObjects) : MacroLocationDatabase {

    override fun isAvailable(uuid: String): Boolean = getFromDatabase(uuid).isNotEmpty()

    override fun get(uuid: String) = getFromDatabase(uuid).firstOrNull()?.run { convert() }


    private fun getFromDatabase(uuid: String) = ao.find(MacroLocationEntity::class.java, Query.select().where("UUID = ?", uuid))

    override fun put(uuid: String, data: MacroLocation) {
        val entity = ao.findByUuid(uuid) ?: ao.create(MacroLocationEntity::class.java)
        entity.uuid = uuid
        entity.page = data.pageId
        entity.space = data.spaceKey
        entity.save()
    }

    override fun getAll(): List<MacroLocation> = ao.find(MacroLocationEntity::class.java).map { it.convert() }


    override fun remove(uuid: String) {
        getFromDatabase(uuid).firstOrNull()?.let { ao.delete(it) }
    }

    override fun removeAll() = ao.find(MacroLocationEntity::class.java).forEach { remove(it.uuid) }


    private fun MacroLocationEntity.convert(): MacroLocation {
        return MacroLocation(
                uuid = uuid,
                pageId = page,
                spaceKey = space
        )
    }
}