package com.networkedassets.git4c.data.macro.documents

import com.networkedassets.git4c.data.GlobForMacro
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.macro.documents.item.DocumentsFileIndex

data class DocumentationsMacro(
        val uuid: String,
        val currentBranch: String,
        val revision: String,
        val glob: List<GlobForMacro>,
        val files: List<DocumentsFileIndex>
) {
    constructor(macroSettings: MacroSettings, revision: String, documents: List<DocumentsFileIndex>, glob: List<GlobForMacro>) : this(
            macroSettings.uuid,
            macroSettings.branch,
            revision,
            glob,
            documents
    )
}