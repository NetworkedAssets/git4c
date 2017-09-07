package com.networkedassets.git4c.infrastructure.git

import com.networkedassets.git4c.core.exceptions.ConDocException
import com.networkedassets.git4c.data.Repository
import org.eclipse.jgit.api.Git
import uy.klutter.core.common.exists
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

class GitDiskCache(val gitClient: DefaultGitClient) {

    private val parentTempDirectory = Paths.get(System.getProperty("java.io.tmpdir"), "git_source_plugin_cache").toFile()!!

    init {
        if (parentTempDirectory.exists()) {
            parentTempDirectory.deleteRecursively()
        }
        parentTempDirectory.mkdir()
    }

    private val lockMap = ConcurrentHashMap<String, ReentrantLock>()
    private val fileCache = mutableMapOf<String, Path>()

    private fun createTempDirectory() = try {
        Files.createTempDirectory(parentTempDirectory.toPath(), null)
    } catch (e: IOException) {
        throw ConDocException("Couldn't create a temp dir!", e)
    }

    fun lock(repository: Repository, branch: String): Git {
        lockMap.putIfAbsent(repository.repositoryPath, ReentrantLock())
        val lock = lockMap[repository.repositoryPath]!!
        lock.lock()
        try {
            return findOrCloneFromDiskCache(repository, branch)
        } catch (e: Exception) {
            //Something went wrong with cloning. Unlock and rethrow
            lock.unlock()
            throw e
        }
    }

    private fun findOrCloneFromDiskCache(repository: Repository, branch: String): Git {
        if (fileCache[repository.repositoryPath] != null) {
            return getOrClone(repository, branch)
        } else {
            return clone(repository, branch)
        }
    }

    private fun getOrClone(repository: Repository, branch: String): Git {
        if (fileCache[repository.repositoryPath]!!.exists()) {
            return gitClient.changeBranch(fileCache[repository.repositoryPath]!!, branch)
        } else {
            return clone(repository, branch)
        }
    }

    private fun clone(repository: Repository, branch: String): Git {
        val temp = createTempDirectory()
        fileCache[repository.repositoryPath] = temp
        gitClient.clone(repository, branch, temp)
        return gitClient.changeBranch(temp, branch)
    }

    fun unlock(repository: Repository) {
        lockMap[repository.repositoryPath]?.unlock()
    }

}