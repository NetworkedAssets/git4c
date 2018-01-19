package com.networkedassets.git4c.infrastructure.mocks.core

import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.VerificationStatus.OK
import com.networkedassets.git4c.boundary.outbound.VerificationStatus.SOURCE_NOT_FOUND
import com.networkedassets.git4c.core.business.Commit
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.core.bussiness.ImportedFiles
import com.networkedassets.git4c.core.bussiness.Revision
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.data.CommitInfo
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.Repository
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils.separatorsToUnix
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Plugin that returns files in given directory - used only in tests
 */
class DirectorySourcePlugin : SourcePlugin {

    override fun isBranchMerged(repository: Repository, branch: String, origin: String): Boolean {
        return true
    }

    override fun accuireLock(repositoryPath: String): Boolean {
        return true
    }

    override fun isLocked(repositoryPath: String): Boolean {
        return false
    }

    override fun pull(repository: Repository, branch: String, alreadyLocked: Boolean): ImportedFiles {
        val d = repository.repositoryPath
        val dir = File(d)
        val root = dir.toPath()
        return FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)
                .map { file ->

                    val absolutePath = file.toPath()
                    val content: ByteArray
                    try {
                        content = absolutePath.toFile().readBytes()
                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    }

                    val relativePath = root.relativize(absolutePath)
                    ImportedFileData(separatorsToUnix(relativePath.toString()), root, { "" }, { "" }, { Date() }, { content })
                }.let { ImportedFiles(it, Closeable {}) }
    }

    override fun get(repository: Repository, branch: String, alreadyLocked: Boolean): ImportedFiles {
        return pull(repository, branch)
    }

    override fun revision(macroSettings: MacroSettings, repository: Repository?, alreadyLocked: Boolean): Revision {
        if (macroSettings.repositoryUuid == null) throw VerificationException(VerificationInfo(VerificationStatus.REMOVED))
        if (repository == null) throw VerificationException(VerificationInfo(VerificationStatus.REMOVED))
        return Revision("", Closeable { })
    }

    override fun verify(repository: Repository?): VerificationInfo {
        if (repository == null) return VerificationInfo(VerificationStatus.REMOVED)
        if (repository.repositoryPath.isNullOrBlank()) return (VerificationInfo(SOURCE_NOT_FOUND))
        return VerificationInfo(OK)
    }

    override fun updateFile(repository: Repository, branch: String, file: String, newContent: String, commit: Commit) {
    }

    override fun createNewBranch(repository: Repository, originBranch: String, newBranch: String) {
    }

    override fun pushLocalBranch(repository: Repository, branch: String) {
    }

    override fun resetBranch(repository: Repository, branch: String) {
    }

    override fun removeBranch(repository: Repository, branchName: String) {
    }

    override fun getLocalBranches(repository: Repository) = emptyList<String>()

    override fun getLocation(repository: Repository) = File(repository.repositoryPath)

    override fun getCommitsForFile(repository: Repository, branch: String, file: String): List<CommitInfo> = listOf()

    override val identifier: String get() = "Directory"

    override fun getBranches(repository: Repository?): List<String> = listOf("master")
}
