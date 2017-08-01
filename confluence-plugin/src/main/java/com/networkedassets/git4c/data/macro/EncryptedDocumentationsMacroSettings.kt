package com.networkedassets.git4c.data.macro

data class EncryptedDocumentationsMacroSettings(
        val id: String,
        val repositoryPath: String,
        val credentials: RepositoryAuthorization,
        val branch: String,
        val glob: String,
        val securityKey: String,
        val defaultDocItem: String
)

