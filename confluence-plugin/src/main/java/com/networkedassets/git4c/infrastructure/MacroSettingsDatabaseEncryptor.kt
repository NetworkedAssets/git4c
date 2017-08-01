package com.networkedassets.git4c.infrastructure

import com.networkedassets.git4c.core.bussiness.Database
import com.networkedassets.git4c.core.common.CredentialsEncryptor
import com.networkedassets.git4c.core.datastore.MacroSettingsRepository
import com.networkedassets.git4c.data.macro.EncryptedDocumentationsMacroSettings

class MacroSettingsDatabaseEncryptor(dataStore: Database<EncryptedDocumentationsMacroSettings>, encryptor: CredentialsEncryptor): MacroSettingsEncryptor(dataStore, encryptor), MacroSettingsRepository