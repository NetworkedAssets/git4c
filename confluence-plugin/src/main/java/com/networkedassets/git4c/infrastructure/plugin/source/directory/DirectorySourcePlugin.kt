package com.networkedassets.git4c.infrastructure.plugin.source.directory

import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.boundary.outbound.VerificationStatus.OK
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.bussiness.SourcePlugin.FetchProcess
import com.networkedassets.git4c.data.macro.DocumentationsMacroSettings
import com.networkedassets.git4c.data.macro.ShortDocumentationsMacroSettings
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils.separatorsToUnix
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

/**
 * Plugin that returns files in given directory - used only in tests
 */
class DirectorySourcePlugin : SourcePlugin {

    override fun createFetchProcess(documentationsMacroSettings: DocumentationsMacroSettings) = DirectoryFetchProcess(documentationsMacroSettings)

    override fun verify(documentationsMacroSettings: DocumentationsMacroSettings): VerificationInfo {
        return VerificationInfo(OK)
    }

    override fun verify(documentationsMacroSettings: ShortDocumentationsMacroSettings): VerificationInfo {
        return VerificationInfo(OK)
    }

    override fun revision(documentationsMacroSettings: DocumentationsMacroSettings): String {
        return "1"
    }

    override val identifier: String
        get() = "Directory"

    inner class DirectoryFetchProcess(val documentationsMacroSettings: DocumentationsMacroSettings): FetchProcess {
        override fun fetch(): List<ImportedFileData> {
            val d = documentationsMacroSettings.repositoryPath
            val dir = File(d)
            val root = dir.toPath()
            return FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)
                    .map { file ->

                        val absolutePath = file.toPath()
                        val content: String
                        try {
                            content = FileUtils.readFileToString(absolutePath.toFile(), Charset.defaultCharset())
                        } catch (e: IOException) {
                            throw RuntimeException(e)
                        }

                        val relativePath = root.relativize(absolutePath)
                        ImportedFileData(separatorsToUnix(relativePath.toString()), root, "", "", Date(), content)
                    }
        }

        override fun close() = Unit
    }

    override fun getBranches(documentationsMacroSettings: DocumentationsMacroSettings): List<String> = listOf("master")
}
