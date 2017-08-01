package com.networkedassets.git4c.infrastructure.git

import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.data.macro.DocumentationsMacroSettings
import com.networkedassets.git4c.data.macro.ShortDocumentationsMacroSettings
import java.nio.file.Path

interface GitClient {

    fun verify(documentationsMacroSettings: DocumentationsMacroSettings): VerificationInfo

    fun verify(documentationsMacroSettings: ShortDocumentationsMacroSettings): VerificationInfo

    fun fetchRawData(documentationsMacroSettings: DocumentationsMacroSettings, temp: Path): List<ImportedFileData>

    fun revision(documentationsMacroSettings: DocumentationsMacroSettings): String

    fun clone(documentationsMacroSettings: DocumentationsMacroSettings, path: Path)

    fun getBranches(documentationsMacroSettings: DocumentationsMacroSettings): List<String>

    fun changeBranch(dir: Path, documentationsMacroSettings: DocumentationsMacroSettings)

    fun fetchRawData(path: Path): List<ImportedFileData>
}
