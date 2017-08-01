package com.networkedassets.git4c.infrastructure.plugin.source.git

import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.bussiness.SourcePlugin.FetchProcess
import com.networkedassets.git4c.core.exceptions.ConDocException
import com.networkedassets.git4c.data.macro.DocumentationsMacroSettings
import com.networkedassets.git4c.data.macro.ShortDocumentationsMacroSettings
import com.networkedassets.git4c.infrastructure.git.GitClient
import uy.klutter.core.common.deleteRecursively
import uy.klutter.core.common.exists
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

class GitSourcePlugin(
        private val gitClient: GitClient
) : SourcePlugin {

    val parentTempDirectory = Paths.get(System.getProperty("java.io.tmpdir"), "git_source_plugin_cache").toFile()!!

    init {
        if (parentTempDirectory.exists()) {
            parentTempDirectory.deleteRecursively()
        }
        parentTempDirectory.mkdir()
    }

    val lockMap = ConcurrentHashMap<String, ReentrantLock>()
    val fileCache = mutableMapOf<String, Path>()

    override val identifier = "git-source"

    override fun createFetchProcess(documentationsMacroSettings: DocumentationsMacroSettings): FetchProcess {
        val path = createTempDirectory()
        return GitFetchProcess(documentationsMacroSettings, path)
    }

    override fun verify(documentationsMacroSettings: DocumentationsMacroSettings): VerificationInfo {
        return gitClient.verify(documentationsMacroSettings)
    }

    override fun verify(documentationsMacroSettings: ShortDocumentationsMacroSettings): VerificationInfo {
        return gitClient.verify(documentationsMacroSettings)
    }

    override fun revision(documentationsMacroSettings: DocumentationsMacroSettings): String {
        return gitClient.revision(documentationsMacroSettings)
    }

    override fun getBranches(documentationsMacroSettings: DocumentationsMacroSettings): List<String> {
        return gitClient.getBranches(documentationsMacroSettings)
    }

    private fun createTempDirectory() = try {
        Files.createTempDirectory(parentTempDirectory.toPath(), null)
    } catch (e: IOException) {
        throw ConDocException("Couldn't create a temp dir!", e)
    }

    inner class GitFetchProcess(val documentationsMacroSettings: DocumentationsMacroSettings, val temp: Path): FetchProcess {

        val lock: ReentrantLock
        lateinit var dir: Path
        var closed = false

        init {
            lockMap.putIfAbsent(documentationsMacroSettings.repositoryPath, ReentrantLock())
            lock = lockMap[documentationsMacroSettings.repositoryPath]!!
        }

        override fun fetch(): List<ImportedFileData> {
            if (closed) {
                throw IllegalStateException("GitFetchProcess was closed")
            }

            lock.lock()

            if (fileCache[documentationsMacroSettings.repositoryPath] != null) {
                //We already have directory
                if (fileCache[documentationsMacroSettings.repositoryPath]!!.exists()) {
                    //And it exists - let's remove directory that was created for us - we don't need it
                    temp.deleteRecursively()
                } else {
                    //Weird - we're supposed to have directory but it doesn't exist. Let's use directory that was
                    //created for us then
                    fileCache[documentationsMacroSettings.repositoryPath] = temp
                    gitClient.clone(documentationsMacroSettings, temp)
                }
            } else {
                fileCache[documentationsMacroSettings.repositoryPath] = temp
                gitClient.clone(documentationsMacroSettings, temp)
            }

            dir = fileCache[documentationsMacroSettings.repositoryPath]!!

            gitClient.changeBranch(dir, documentationsMacroSettings)
            return gitClient.fetchRawData(dir)
        }

        override fun close() {
            if (!closed && lock.isHeldByCurrentThread) {
                lock.unlock()
            }
            closed = true
        }
    }
}
