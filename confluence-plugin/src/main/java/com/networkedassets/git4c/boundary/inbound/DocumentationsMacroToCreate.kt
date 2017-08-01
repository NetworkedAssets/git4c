package com.networkedassets.git4c.boundary.inbound

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

data class DocumentationMacroToCreate(
        val sourceRepositoryUrl: String,
        val branch: String,
        val glob: String,
        val credentials: AuthorizationData,
        val defaultDocItem: String
)

data class DocumentationToGetBranches(
        val sourceRepositoryUrl: String,
        val credentials: AuthorizationData
)

data class SshKeyAuthorization(
        val sshKey: String
) : AuthorizationData

data class UsernamePasswordAuthorization(
        val username: String,
        val password: String
) : AuthorizationData

class NoAuth : AuthorizationData

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = UsernamePasswordAuthorization::class, name = "USERNAMEPASSWORD"),
        JsonSubTypes.Type(value = SshKeyAuthorization::class, name = "SSHKEY"),
        JsonSubTypes.Type(value = NoAuth::class, name = "NOAUTH")
)
interface AuthorizationData