package com.networkedassets.git4c.infrastructure.database

import com.networkedassets.git4c.core.datastore.EncryptedMacroSettingsRepository
import com.networkedassets.git4c.data.macro.EncryptedDocumentationsMacroSettings
import com.networkedassets.git4c.infrastructure.database.ao.EncryptedDocumentationsMacroSettingsDBService

class EncryptedDocumentationsMacroSettingsActiveObjectDatabase(val mDbDocumentations: EncryptedDocumentationsMacroSettingsDBService): EncryptedMacroSettingsRepository {

    override fun isAvailable(id: String) = mDbDocumentations.isAvailable(id)

    override fun get(id: String) = mDbDocumentations.getSettings(id)

    override fun put(id: String, data: EncryptedDocumentationsMacroSettings) = mDbDocumentations.add(data)

    override fun remove(id: String) = mDbDocumentations.remove(id)

    override fun removeAll() = mDbDocumentations.removeAll()

}