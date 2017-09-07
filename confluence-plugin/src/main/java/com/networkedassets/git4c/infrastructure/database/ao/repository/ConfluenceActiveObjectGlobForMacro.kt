package com.networkedassets.git4c.infrastructure.database.ao.repository

import com.atlassian.activeobjects.external.ActiveObjects
import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.data.GlobForMacro
import com.networkedassets.git4c.infrastructure.database.ao.GlobEntity
import net.java.ao.Query

class ConfluenceActiveObjectGlobForMacro(val ao: ActiveObjects) : GlobForMacroDatabase {

    override fun getByMacro(macroUuid: String): List<GlobForMacro> =
            ao.find(GlobEntity::class.java, Query.select().where("MACRO = ?", macroUuid)).map { it.convert() }

    override fun isAvailable(uuid: String): Boolean = getFromDatabase(uuid).isNotEmpty()

    override fun get(uuid: String) = getFromDatabase(uuid).firstOrNull()?.run { convert() }

    private fun getFromDatabase(uuid: String) = ao.find(GlobEntity::class.java, Query.select().where("UUID = ?", uuid))

    override fun insert(uuid: String, data: GlobForMacro) {
        val entity = ao.create(GlobEntity::class.java)
        entity.uuid = uuid
        entity.macro = data.macroSettingsUuid
        entity.glob = data.glob
        entity.save()
    }

    override fun getAll(): List<GlobForMacro> = ao.find(GlobEntity::class.java).map { it.convert() }

    override fun update(uuid: String, data: GlobForMacro) {
        remove(uuid)
        insert(uuid, data)
    }

    override fun remove(uuid: String) {
        getFromDatabase(uuid).firstOrNull()?.let { ao.delete(it) }
    }

    override fun removeAll() = ao.find(GlobEntity::class.java).forEach { remove(it.uuid) }


    private fun GlobEntity.convert(): GlobForMacro {
        return GlobForMacro(
                uuid = uuid,
                macroSettingsUuid = macro,
                glob = glob
        )
    }
}




