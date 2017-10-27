package com.networkedassets.git4c.boundary.inbound

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME

data class DocumentationMacro(
        val repositoryDetails: RepositoryDetails,
        val branch: String,
        val glob: List<String>,
        val defaultDocItem: String,
        val extractor: ExtractorData?
)

data class RepositoryDetails(
        val repository: RepositoryToCreate
)

@JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, property = "type")
@JsonSubTypes(
        Type(value = CustomRepository::class, name = "CUSTOM"),
        Type(value = PredefinedRepositoryToCreate::class, name = "PREDEFINED"),
        Type(value = ExistingRepository::class, name = "EXISTING")

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


@JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, property = "type")
@JsonSubTypes(
        Type(value = LineNumbers::class, name = "LINENUMBERS"),
        Type(value = Method::class, name = "METHOD")

)
abstract class ExtractorData(
)

data class LineNumbers(
        val start: Int,
        val end: Int
) : ExtractorData()

data class Method(
        val method: String
): ExtractorData()