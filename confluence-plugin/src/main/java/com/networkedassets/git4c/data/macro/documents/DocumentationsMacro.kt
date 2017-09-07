package com.networkedassets.git4c.data.macro.documents

import com.networkedassets.git4c.data.GlobForMacro
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem

data class DocumentationsMacro(
        val uuid: String,
        val currentBranch: String,
        val revision: String,
        val glob: List<GlobForMacro>,
        val files: List<DocumentsItem>
) {
    constructor(macroSettings: MacroSettings, revision: String, documents: List<DocumentsItem>, glob: List<GlobForMacro>) : this(
            macroSettings.uuid,
            macroSettings.branch,
            revision,
            glob,
            documents
    )
}