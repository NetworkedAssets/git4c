package com.networkedassets.git4c.infrastructure

import com.networkedassets.git4c.core.bussiness.DataStore
import com.networkedassets.git4c.core.common.CredentialsEncryptor
import com.networkedassets.git4c.data.macro.*
import org.apache.commons.lang3.RandomStringUtils

abstract class MacroSettingsEncryptor(
        store: DataStore<EncryptedDocumentationsMacroSettings>,
        cipher: CredentialsEncryptor
): Encryptor<DocumentationsMacroSettings, EncryptedDocumentationsMacroSettings>(store, cipher) {

    override fun get(id: String): DocumentationsMacroSettings? {
        val macroSettings = dataStore.get(id) ?: return null
        val credentials = when (macroSettings.credentials) {
            is UsernameAndPasswordCredentials -> {
                UsernameAndPasswordCredentials(
                        username = macroSettings.credentials.username,
                        password = encryptor.decrypt(macroSettings.credentials.password, macroSettings.securityKey)
                )
            }
            is SshKeyCredentials -> {
                SshKeyCredentials(encryptor.decrypt(macroSettings.credentials.sshKey, macroSettings.securityKey))
            }
            is NoAuthCredentials -> {
                macroSettings.credentials
            }
            else -> {
                throw RuntimeException("All credential settings are null")
            }
        }
        return DocumentationsMacroSettings(id, macroSettings.repositoryPath, credentials, macroSettings.branch, macroSettings.glob, macroSettings.defaultDocItem)

    }

    override fun put(id: String, data: DocumentationsMacroSettings) {

        val key = RandomStringUtils.randomAscii(50)
        val encryptor = SimpleCredentialsEncryptor()
        val credentials = when (data.credentials) {
            is UsernameAndPasswordCredentials -> {
                UsernameAndPasswordCredentials(
                        username = data.credentials.username,
                        password = encryptor.encrypt(data.credentials.password, key)
                )
            }
            is SshKeyCredentials -> {
                SshKeyCredentials(encryptor.encrypt(data.credentials.sshKey, key))
            }
            is NoAuthCredentials -> {
                data.credentials
            }
            else -> {
                throw RuntimeException("All credential settings are null")
            }
        }
        dataStore.put(id, EncryptedDocumentationsMacroSettings(id, data.repositoryPath, credentials, data.branch, data.glob, key, data.defaultDocItem))
    }

    override fun remove(id: String) {
        dataStore.remove(id)
    }

    override fun isAvailable(id: String): Boolean {
        return dataStore.get(id) != null
    }

    override fun removeAll() {
        dataStore.removeAll()
    }
}