package com.networkedassets.git4c.infrastructure.git

import com.networkedassets.git4c.core.exceptions.ConDocException
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.utils.debug
import com.networkedassets.git4c.utils.error
import com.networkedassets.git4c.utils.getLogger
import com.networkedassets.git4c.utils.info
import org.eclipse.jgit.api.Git
import uy.klutter.core.common.deleteRecursively
import uy.klutter.core.common.exists
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.Semaphore

class GitDiskCache(val gitClient: DefaultGitClient) {

    private val parentTempDirectory = Paths.get(System.getProperty("java.io.tmpdir"), "git_source_plugin_cache").toFile()!!

    init {
        if (parentTempDirectory.exists()) {
            parentTempDirectory.deleteRecursively()
        }
        parentTempDirectory.mkdir()
    }

    private val lockMap = Collections.synchronizedMap(HashMap<String, Semaphore>())
    private val fileCache = Collections.synchronizedMap(mutableMapOf<String, Path>())

    val log = getLogger()

    private fun createTempDirectory() = try {
        Files.createTempDirectory(parentTempDirectory.toPath(), null)
    } catch (e: IOException) {
        throw ConDocException("Couldn't create a temp dir!", e)
    }

    fun lock(repository: Repository, branch: String, alreadyLocked: Boolean = false): Git {
        log.debug { "Demend of lock for RepositoryPath=${repository.repositoryPath}" }
        lockMap.putIfAbsent(repository.repositoryPath, Semaphore(1, true))
        val lock = lockMap[repository.repositoryPath]!!
        if (!alreadyLocked) {
            log.debug { "LOCK demend for repository with RepositoryPath=${repository.repositoryPath}. Awaiting to be locked..." }
            lock.acquire()
            log.debug { "LOCK Accepted for repository with RepositoryPath=${repository.repositoryPath}" }
        } else {
            log.debug { "LOCK is defined by operation as already accuired. Will not set a lock and start operations for RepositoryPath=${repository.repositoryPath}." }
        }
        if (!isLocked(repository.repositoryPath)) {
            log.info { "LOCK - There is defined that lock is already accuired but in true the lock is not present for RepositoryPath=${repository.repositoryPath} !!! Will await for the lock as already can not retry at this step!" }
            lock.acquire()
        }

        try {
            return findOrCloneFromDiskCache(repository, branch)
        } catch (e: Exception) {
            log.error({ "LOCK - ERROR - Lock will be released for RepositoryPath=${repository.repositoryPath}" }, e)
            lock.release()
            throw e
        }
    }

    private fun findOrCloneFromDiskCache(repository: Repository, branch: String): Git {
        if (fileCache[repository.repositoryPath] != null) {
            log.debug { "RepositoryPath=${repository.repositoryPath} has been found in the internal cache" }
            return getOrClone(repository, branch)
        } else {
            log.debug { "RepositoryPath=${repository.repositoryPath} has not been found in the internal cache" }
            return clone(repository, branch)
        }
    }

    private fun getOrClone(repository: Repository, branch: String): Git {
        if (fileCache[repository.repositoryPath]!!.exists()) {
            log.debug { "RepositoryPath=${repository.repositoryPath} has been found in the cache so will try to change branch" }
            return gitClient.changeBranch(fileCache[repository.repositoryPath]!!, branch)
        } else {
            log.debug { "RepositoryPath=${repository.repositoryPath} has not been found in the cache so will try to clone" }
            return clone(repository, branch)
        }
    }

    private fun clone(repository: Repository, branch: String): Git {
        log.debug { "RepositoryPath=${repository.repositoryPath} will be cloned" }
        val temp = createTempDirectory()
        log.debug { "RepositoryPath=${repository.repositoryPath} has a LocalPath=${temp} for clone purpose" }
        fileCache[repository.repositoryPath] = temp
        gitClient.clone(repository, branch, temp)
        log.debug { "RepositoryPath=${repository.repositoryPath} has been cloned so now changing a branch to RepositoryBranch=${branch}" }
        return gitClient.changeBranch(temp, branch)
    }

    fun unlock(repository: Repository) {
        log.debug { "RepositoryPath=${repository.repositoryPath} will be unlocked!" }
        lockMap[repository.repositoryPath]?.release()
    }

    fun removeCache(repository: Repository) {
        log.debug { "RepositoryPath=${repository.repositoryPath} will be removed from the cache!!!" }
        fileCache[repository.repositoryPath]?.deleteRecursively()
        fileCache.remove(repository.repositoryPath)
    }

    fun isLocked(repositoryPath: String): Boolean {
        lockMap.putIfAbsent(repositoryPath, Semaphore(1, true))
        val isLocked = lockMap[repositoryPath]?.availablePermits() ?: 1 == 0
        // log.debug { "RepositoryPath=${repository.repositoryPath} is marked as locked: ${isLocked}" }
        return isLocked
    }

    fun accuireLock(repositoryPath: String): Boolean {
        log.debug { "LOCK - RepositoryPath=${repositoryPath} will be try to be locked" }
        if (lockMap[repositoryPath]?.availablePermits() ?: 1 == 0) {
            log.debug { "LOCK - RepositoryPath=${repositoryPath} is already locked so there is no possibility to lock it now" }
            return false
        }
        lockMap.putIfAbsent(repositoryPath, Semaphore(1, true))
        val isLocked = lockMap[repositoryPath]!!.tryAcquire()
        log.debug { "LOCK - RepositoryPath=${repositoryPath} has been locked: ${isLocked}" }
        return isLocked
    }

}