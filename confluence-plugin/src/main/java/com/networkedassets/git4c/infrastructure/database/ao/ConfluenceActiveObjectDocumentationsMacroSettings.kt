package com.networkedassets.git4c.infrastructure.database.ao

import com.atlassian.activeobjects.external.ActiveObjects
import com.networkedassets.git4c.data.macro.*
import net.java.ao.Query

class ConfluenceActiveObjectDocumentationsMacroSettings(val ao: ActiveObjects) : EncryptedDocumentationsMacroSettingsDBService {

    override fun isAvailable(uuid: String): Boolean {
        return getSettingsForId(uuid).isNotEmpty()
    }

    override fun getSettings(uuid: String) =
            getDBSettingForId(uuid)?.run {
                EncryptedDocumentationsMacroSettings(
                        id = uuid,
                        branch = branch,
                        glob = glob,
                        repositoryPath = path,
                        credentials = entityCredentialsToCore(auth),
                        securityKey = securityKey,
                        defaultDocItem = defaultDocItem

                )
            }

    override fun add(documentationsMacroSettings: EncryptedDocumentationsMacroSettings) {
        val settings = ao.create(DocumentationsMacroSettingsEntity::class.java)
        val authEntity = coreCredentialsToEntity(documentationsMacroSettings.credentials)

        settings.uuid = documentationsMacroSettings.id
        settings.branch = documentationsMacroSettings.branch
        settings.glob = documentationsMacroSettings.glob
        settings.path = documentationsMacroSettings.repositoryPath
        settings.securityKey = documentationsMacroSettings.securityKey
        settings.auth = authEntity
        settings.defaultDocItem = documentationsMacroSettings.defaultDocItem

        authEntity.save()
        settings.save()
    }

    private fun coreCredentialsToEntity(credentials: RepositoryAuthorization) = when(credentials) {
        is NoAuthCredentials -> ao.create(NoAuthEntity::class.java)
        is UsernameAndPasswordCredentials -> {
            val uap = ao.create(UsernamePasswordAuthEntity::class.java)
            uap.username = credentials.username
            uap.password = credentials.password
            uap
        }
        is SshKeyCredentials -> {
            val skc = ao.create(SSHAuthEntity::class.java)
            skc.key = credentials.sshKey
            skc
        }
        else -> throw RuntimeException("Unknown credentials type: ${credentials.javaClass.name}")
    }

    private fun entityCredentialsToCore(credentials: AuthEntity) = when (credentials) {
        is UsernamePasswordAuthEntity -> {
            UsernameAndPasswordCredentials(
                    username = credentials.username,
                    password = credentials.password
            )
        }
        is SSHAuthEntity -> SshKeyCredentials(credentials.key)
        is NoAuthEntity -> NoAuthCredentials()
        else -> throw RuntimeException("All credential settings are null")
    }

    private fun getDBSettingForId(uuid: String): DocumentationsMacroSettingsEntity? = getSettingsForId(uuid).firstOrNull()

    private fun getSettingsForId(uuid: String) = ao.find(DocumentationsMacroSettingsEntity::class.java, Query.select().where("uuid = ?", uuid))

    override fun remove(uuid: String) {
        val setting = getDBSettingForId(uuid)
        if (setting != null) {
            ao.delete(setting.auth, setting)
        }
    }

    override fun removeAll() = ao.find(DocumentationsMacroSettingsEntity::class.java).forEach { remove(it.uuid) }

}
