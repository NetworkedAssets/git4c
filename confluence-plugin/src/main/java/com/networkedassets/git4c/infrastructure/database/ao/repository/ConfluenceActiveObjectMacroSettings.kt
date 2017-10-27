package com.networkedassets.git4c.infrastructure.database.ao.repository

import com.atlassian.activeobjects.external.ActiveObjects
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.infrastructure.database.ao.MacroSettingsEntity
import net.java.ao.Query

class ConfluenceActiveObjectMacroSettings(val ao: ActiveObjects) : MacroSettingsDatabase {

    override fun getByRepository(uuid: String): List<MacroSettings> =
            ao.find(MacroSettingsEntity::class.java, Query.select().where("REPOSITORY = ?", uuid)).map { it.convert() }

    override fun isAvailable(uuid: String): Boolean = getFromDatabase(uuid).isNotEmpty()

    override fun get(uuid: String) = getFromDatabase(uuid).firstOrNull()?.run { convert() }


    private fun getFromDatabase(uuid: String) = ao.find(MacroSettingsEntity::class.java, Query.select().where("UUID = ?", uuid))

    override fun insert(uuid: String, data: MacroSettings) {
        val settings = ao.create(MacroSettingsEntity::class.java)
        settings.uuid = uuid
        settings.branch = data.branch
        settings.repository = data.repositoryUuid
        settings.defaultDocItem = data.defaultDocItem
        settings.extractor = data.extractorDataUuid
        settings.save()
    }

    override fun getAll(): List<MacroSettings> = ao.find(MacroSettingsEntity::class.java).map { it.convert() }

    override fun update(uuid: String, data: MacroSettings) {
        remove(uuid)
        insert(uuid, data)
    }

    override fun remove(uuid: String) {
        getFromDatabase(uuid).firstOrNull()?.let { ao.delete(it) }
    }

    override fun removeAll() = ao.find(MacroSettingsEntity::class.java).forEach { remove(it.uuid) }


    private fun MacroSettingsEntity.convert(): MacroSettings {
        return MacroSettings(
                uuid = uuid,
                branch = branch,
                repositoryUuid = repository,
                defaultDocItem = defaultDocItem,
                extractorDataUuid = extractor
        )
    }
}