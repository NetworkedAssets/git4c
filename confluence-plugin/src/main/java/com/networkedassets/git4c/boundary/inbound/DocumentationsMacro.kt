package com.networkedassets.git4c.boundary.inbound

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

data class DocumentationMacro(
        val repositoryDetails: RepositoryDetails,
        val branch: String,
        val glob: List<String>,
        val defaultDocItem: String,
        val method: String?
)

data class RepositoryDetails(
        val repository: RepositoryToCreate
)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = CustomRepository::class, name = "CUSTOM"),
        JsonSubTypes.Type(value = PredefinedRepositoryToCreate::class, name = "PREDEFINED"),
        JsonSubTypes.Type(value = ExistingRepository::class, name = "EXISTING")

)
abstract class RepositoryToCreate(
)

data class CustomRepository(
        val url: String,
        val credentials: AuthorizationData
) : RepositoryToCreate()

data class PredefinedRepositoryToCreate(
        val uuid: String
) : RepositoryToCreate()

data class ExistingRepository(
        val uuid: String
) : RepositoryToCreate()