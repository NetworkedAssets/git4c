package com.networkedassets.git4c.infrastructure

import com.networkedassets.git4c.core.bussiness.Cache
import com.networkedassets.git4c.core.common.CredentialsEncryptor
import com.networkedassets.git4c.core.datastore.MacroSettingsCache
import com.networkedassets.git4c.data.macro.EncryptedDocumentationsMacroSettings

class MacroSettingsCacheEncryptor(dataStore: Cache<EncryptedDocumentationsMacroSettings>, encryptor: CredentialsEncryptor): MacroSettingsEncryptor(dataStore, encryptor), MacroSettingsCache