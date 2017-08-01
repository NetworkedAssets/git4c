package com.networkedassets.git4c.data.macro

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

data class DocumentationsMacroSettings(
        val id: String,
        val repositoryPath: String,
        val credentials: RepositoryAuthorization,
        val branch: String,
        val glob: String,
        val defaultDocItem: String
)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = UsernameAndPasswordCredentials::class, name = "USERNAMEPASSWORD"),
        JsonSubTypes.Type(value = SshKeyCredentials::class, name = "SSHKEY"),
        JsonSubTypes.Type(value = NoAuthCredentials::class, name = "NOAUTH")
)
interface RepositoryAuthorization

data class UsernameAndPasswordCredentials(
        val username: String,
        val password: String
) : RepositoryAuthorization

data class SshKeyCredentials(
        val sshKey: String
) : RepositoryAuthorization

class NoAuthCredentials : RepositoryAuthorization