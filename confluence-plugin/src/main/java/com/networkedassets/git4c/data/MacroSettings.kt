package com.networkedassets.git4c.data

data class MacroSettings(
        val uuid: String,
        val repositoryUuid: String?,
        val branch: String,
        val defaultDocItem: String,
        val extractorDataUuid: String?,
        val rootDirectory: String?,
        var type: MacroType?
) {
    constructor(uuid: String, repositoryUuid: String?, branch: String, defaultDocItem: String, extractorDataUuid: String?, rootDirectory: String?)
            : this(uuid, repositoryUuid, branch, defaultDocItem, extractorDataUuid, rootDirectory, null)
}

enum class MacroType {
    MULTIFILE,
    SINGLEFILE
}
