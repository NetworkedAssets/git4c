package com.networkedassets.git4c.data

data class MacroSettings(
        val uuid: String,
        val repositoryUuid: String?,
        val branch: String,
        val defaultDocItem: String,
        val extractorDataUuid: String?
)

