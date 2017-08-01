package com.networkedassets.git4c.core.bussiness

import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.data.macro.DocumentationsMacroSettings
import com.networkedassets.git4c.data.macro.ShortDocumentationsMacroSettings
import java.io.Closeable

interface SourcePlugin : Plugin {

    fun createFetchProcess(documentationsMacroSettings: DocumentationsMacroSettings): FetchProcess

    fun verify(documentationsMacroSettings: DocumentationsMacroSettings): VerificationInfo

    fun verify(documentationsMacroSettings: ShortDocumentationsMacroSettings): VerificationInfo

    fun revision(documentationsMacroSettings: DocumentationsMacroSettings): String

    fun getBranches(documentationsMacroSettings: DocumentationsMacroSettings): List<String>

    interface FetchProcess: Closeable {
        fun fetch(): List<ImportedFileData>
        override fun close()
    }
}
