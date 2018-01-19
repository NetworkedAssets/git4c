package com.networkedassets.git4c.infrastructure.database.ao.repository

import com.atlassian.activeobjects.external.ActiveObjects
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.MacroType
import com.networkedassets.git4c.infrastructure.database.ao.MacroSettingsEntity
import com.networkedassets.git4c.utils.ActiveObjectsUtils.findByUuid
import net.java.ao.Query

class ConfluenceActiveObjectMacroSettings(val ao: ActiveObjects) : MacroSettingsDatabase {

    override fun getByRepository(uuid: String): List<MacroSettings> =
            ao.find(MacroSettingsEntity::class.java, Query.select().where("REPOSITORY = ?", uuid)).map { it.convert() }

    override fun isAvailable(uuid: String): Boolean = getFromDatabase(uuid).isNotEmpty()

    override fun get(uuid: String) = getFromDatabase(uuid).firstOrNull()?.run { convert() }


    private fun getFromDatabase(uuid: String) = ao.find(MacroSettingsEntity::class.java, Query.select().where("UUID = ?", uuid))

    override fun put(uuid: String, data: MacroSettings) {
        val settings = ao.findByUuid(uuid) ?: ao.create(MacroSettingsEntity::class.java)
        settings.uuid = uuid
        settings.branch = data.branch
        settings.repository = data.repositoryUuid
        settings.defaultDocItem = data.defaultDocItem
        settings.extractor = data.extractorDataUuid
        settings.rootDirectory = data.rootDirectory
        settings.type = getMacroSettingsType(data.type)
        settings.save()
    }

    override fun getAll(): List<MacroSettings> = ao.find(MacroSettingsEntity::class.java).map { it.convert() }

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
                extractorDataUuid = extractor,
                rootDirectory = rootDirectory,
                type = getMacroType(type)
        )
    }

    private fun getMacroType(type: String?): MacroType? {
        if (type == null) return null
        if (type == "SINGLEFILE") return MacroType.SINGLEFILE
        if (type == "MULTIFILE") return MacroType.MULTIFILE
        return null
    }

    private fun getMacroSettingsType(type: MacroType?): String? {
        if (type == null) return null
        if (type == MacroType.SINGLEFILE) return "SINGLEFILE"
        if (type == MacroType.MULTIFILE) return "MULTIFILE"
        return null
    }
}