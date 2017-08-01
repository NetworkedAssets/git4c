package com.networkedassets.git4c.data.macro.documents

import com.networkedassets.git4c.data.macro.DocumentationsMacroSettings
import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem

data class DocumentationsMacro(
        val uuid: String,
        val currentBranch: String,
        val revision: String,
        val glob: String,
        val files: List<DocumentsItem>
) {
    constructor(documentationsMacroSettings: DocumentationsMacroSettings, revision: String, documents: List<DocumentsItem>) : this(
            documentationsMacroSettings.id,
            documentationsMacroSettings.branch,
            revision,
            documentationsMacroSettings.glob,
            documents
    )
}