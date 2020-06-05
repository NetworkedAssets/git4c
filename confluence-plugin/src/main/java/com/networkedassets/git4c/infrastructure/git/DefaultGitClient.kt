package com.networkedassets.git4c.infrastructure.git

import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.boundary.outbound.VerificationStatus.*
import com.networkedassets.git4c.core.business.Commit
import com.networkedassets.git4c.core.bussiness.ImportedFiles
import com.networkedassets.git4c.core.bussiness.Revision
import com.networkedassets.git4c.core.exceptions.ConDocException
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.data.*
import com.networkedassets.git4c.infrastructure.plugin.source.git.ImportedSourceFile
import com.networkedassets.git4c.utils.ThrowableUtils.isCausedBy
import com.networkedassets.git4c.utils.debug
import com.networkedassets.git4c.utils.error
import com.networkedassets.git4c.utils.getLogger
import com.networkedassets.git4c.utils.warn
import org.apache.commons.lang3.StringUtils
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.api.TransportCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.api.errors.NoHeadException
import org.eclipse.jgit.api.errors.RefNotAdvertisedException
import org.eclipse.jgit.errors.NoRemoteRepositoryException
import org.eclipse.jgit.internal.JGitText
import org.eclipse.jgit.lib.BatchingProgressMonitor
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.transport.*
import org.eclipse.jgit.util.FS
import java.io.Closeable
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Collections.emptySet
import java.util.concurrent.TimeUnit

class DefaultGitClient() : GitClient {

    private val log = getLogger()

    private val cache = GitDiskCache(this)

    private val FILE_WRITE_ATTEMPT = "Unauthorized file write attempt"

    @Throws(VerificationException::class)
    override fun revision(repository: Repository, branch: String, alreadyLocked: Boolean): Revision {
        log.debug { "GIT - Checking revision for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch}" }
        val branchStatus = branchRevision(repository, branch, alreadyLocked)
        log.debug { "GIT - for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch} there is Revision=${branchStatus.revision}" }
        return branchStatus
    }

    @Throws(VerificationException::class)
    private fun branchRevision(repository: Repository, branch: String, alreadyLocked: Boolean): Revision {
        log.debug { "GIT - Checking revision for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch}" }
        val repositoryBranch = getBranchOrDefault(branch)
        val git = cache.lock(repository, branch, alreadyLocked)
        val command = git.fetch().addAuthData(repository).setProgressMonitor(GitTaskProgressMonitor(repository.repositoryPath))
        try {
            val refs = command.call()
            val revs = getAllBranchesAndTags(refs)
            val commit = parseLastCommitForBranch(refs, repositoryBranch)
            if (!revs.contains(repositoryBranch)) throw VerificationException(VerificationInfo(WRONG_BRANCH))
            log.debug { "GIT - Checking revision for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch} has been done" }
            return closeableRevision(commit, repository)
        } catch (e: Exception) {
            cache.unlock(repository)
            throwRepositoryError(repository, e)
        }
        log.error { "There was no possibility to check revision for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch}" }
        throw VerificationException(VerificationInfo(SOURCE_NOT_FOUND))
    }

    override fun isLocked(repositoryPath: String): Boolean {
        return cache.isLocked(repositoryPath)
    }

    override fun isBranchMerged(repository: Repository, branch: String, origin: String): Boolean {
        val git = cache.lock(repository, branch)
        pull(repository, origin, true)
        pull(repository, branch, true)
        val gitRepository = git.repository

        try {

            RevWalk(gitRepository).use { revWalk ->
                val masterHead = revWalk.parseCommit(gitRepository.resolve("refs/heads/$origin"))
                val otherHead = revWalk.parseCommit(gitRepository.resolve("refs/heads/$branch"))
                return revWalk.isMergedInto(otherHead, masterHead)
            }

        } catch (e: Exception) {
            log.error({ "Error during checking if branches are merged" }, e)
            throwRepositoryError(repository, e)
        } finally {
            cache.unlock(repository)
        }

    }

    override fun accuireLock(repositoryPath: String): Boolean {
        return cache.accuireLock(repositoryPath)
    }

    private fun closeableRevision(commit: String, repository: Repository): Revision {
        return Revision(commit, Closeable { cache.unlock(repository) })
    }

    @Throws(VerificationException::class)
    private fun throwRepositoryError(repository: Repository, e: Exception): Nothing {
        log.error({ "Problem with RepositoryPath: ${repository.repositoryPath}" }, e)

        if (e.message?.contains(JGitText.get().notAuthorized) == true) {
            throw VerificationException(VerificationInfo(WRONG_CREDENTIALS))
        }

        if (e.message?.contains("Auth fail") == true) {
            throw VerificationException(VerificationInfo(WRONG_CREDENTIALS))
        }

        if (e.message?.contains(JGitText.get().notFound) == true) {
            throw VerificationException(VerificationInfo(SOURCE_NOT_FOUND))
        }

        if (e.message?.contains("CAPTCHA") == true) {
            throw VerificationException(VerificationInfo(CAPTCHA_REQUIRED))
        }

        if (e.isCausedBy(JSchException::class) && e.message.orEmpty().contains("invalid privatekey")) {
            throw VerificationException(VerificationInfo(WRONG_KEY_FORMAT))
        }

        if (e.message?.contains(JGitText.get().noCredentialsProvider) == true) {
            throw VerificationException(VerificationInfo(WRONG_CREDENTIALS))
        }

        if (e.message?.equals(FILE_WRITE_ATTEMPT) == true) {
            throw RuntimeException(FILE_WRITE_ATTEMPT)
        }

        if (e.isCausedBy(NoRemoteRepositoryException::class)) {
            throw VerificationException(VerificationInfo(ACCESS_DENIED))
        }

        if (e.message?.contains(JGitText.get().unknownHost) == true) {
            throw VerificationException(VerificationInfo(UNKNOWN_HOST))
        }

        throw VerificationException(VerificationInfo(SOURCE_NOT_FOUND))
    }

    override fun verify(repository: Repository): VerificationInfo {
        val url = repository.repositoryPath
        log.debug { "Verify of RepositoryPath=${url} will be performed" }
        try {
            Git.lsRemoteRepository().setRemote(url).addAuthData(repository).call()
        } catch (e: GitAPIException) {
            return getRepositoryError(repository, e)
        }
        log.debug { "Verification of RepositoryPath=${url} has been done with status OK." }
        return VerificationInfo(OK)
    }

    private fun getRepositoryError(repository: Repository, e: GitAPIException): VerificationInfo {
        try {
            throwRepositoryError(repository, e)
            return VerificationInfo(SOURCE_NOT_FOUND)
        } catch (e: VerificationException) {
            return e.verification
        }
    }

    companion object {

        //Visible for testing
        public fun <T : TransportCommand<*, *>> T.addAuthData(authType: Repository?): T {
            if (authType is RepositoryWithUsernameAndPassword) {
                val auth = authType
                val provider = UsernamePasswordCredentialsProvider(auth.username, auth.password)
                setCredentialsProvider(provider)
            } else if (authType is RepositoryWithSshKey) {
                prepareSshKeyTransport(authType)
            } else if (authType is RepositoryWithNoAuthorization) {
                //Do nothing
            } else {
                throw RuntimeException("Unknown GitAuthType: " + authType!!::class.java.toString())
            }

            return this
        }

        private fun <T : TransportCommand<*, *>> T.prepareSshKeyTransport(authType: RepositoryWithSshKey): T {
            val auth = authType
            val sshSessionFactory = object : JschConfigSessionFactory() {
                override fun configure(hc: OpenSshConfig.Host, session: Session) {
                    session.setConfig("StrictHostKeyChecking", "no");
                }

                @Throws(JSchException::class)
                override fun createDefaultJSch(fs: FS): JSch {
                    val defaultJSch = super.createDefaultJSch(fs)
                    //THIS IS ESSENTIAL
                    defaultJSch.removeAllIdentity()
                    defaultJSch.addIdentity("prvkey", auth.sshKey.toByteArray(), null, null)
                    return defaultJSch
                }
            }
            setTransportConfigCallback { transport ->
                val sshTransport = transport as SshTransport
                sshTransport.sshSessionFactory = sshSessionFactory
            }
            return this
        }

    }

    private fun getFilesFromDir(dir: File): Collection<File> {

        if (dir.isDirectory) {
            val content = dir.listFiles().filter { !it.name.contains(".git") }

            return content
                    .map({ this.getFilesFromDir(it) })
                    .flatMap({ it })

        } else if (dir.isFile) {
            return setOf(dir)
        } else {
            log.warn { "Not a file and not a directory: ${dir.absolutePath}" }
            return emptySet()
        }

    }

    override fun getBranches(repository: Repository): List<String> {
        log.debug { "Will get branches for RepositoryPath=${repository.repositoryPath}" }
        val url = repository.repositoryPath
        val command = Git.lsRemoteRepository().addAuthData(repository)
                .setRemote(url)
        log.debug { "Getting of all branches has been done for RepositoryPath=${url}" }
        return getAllBranchesAndTags(command.call())
    }

    override fun pull(repository: Repository, branch: String, alreadyLocked: Boolean): ImportedFiles {
        return pull(repository, branch, 0, alreadyLocked)
    }

    private fun pull(repository: Repository, branch: String, retry: Int, alreadyLocked: Boolean): ImportedFiles {
        log.debug { "Will do a git pull operation for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch}" }
        val git = cache.lock(repository, branch, alreadyLocked)
        try {
            git.pull().addAuthData(repository).setProgressMonitor(GitTaskProgressMonitor(repository.repositoryPath)).call()
        } catch (e: NoHeadException) {
            cache.removeCache(repository)
            cache.unlock(repository)
            throw e
        } catch (e: RefNotAdvertisedException) {
            //TAG
            cache.unlock(repository)
        } catch (e: Exception) {
            cache.removeCache(repository)
            cache.unlock(repository)
            log.warn { "There was a problem while try to pull a repository for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch}" }
            if (retry < 3) {
                log.debug { "Will try to retry pull operation for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch}" }
                return pull(repository, branch, retry + 1, alreadyLocked)
            } else {
                log.error({ "Will not pull anymore for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch} as there was an error and retry did not help." }, e)
                throw e
            }
        }
        val answers = getFilesFromDir(git.repository.workTree)
        val answer = answers
                .map { file -> ImportedSourceFile(git, git.repository.workTree, file) }
                .filter { file -> !file.isInternalGitFile() }
                .map { it.convert() }
        return ImportedFiles(answer, Closeable { cache.unlock(repository) })
    }

    override fun get(repository: Repository, branch: String, alreadyLocked: Boolean): ImportedFiles {
        log.debug { "Will get a files for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch}" }
        val git = cache.lock(repository, branch, alreadyLocked)
        val answers = getFilesFromDir(git.repository.workTree)
        val answer = answers
                .map { file -> ImportedSourceFile(git, git.repository.workTree, file) }
                .filter { file -> !file.isInternalGitFile() }
                .map { it.convert() }
        return ImportedFiles(answer, Closeable { cache.unlock(repository) })
    }


    //visible for testing
    fun clone(repository: Repository, branch: String, path: Path): Git {
        log.debug { "GIT - CLONE for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch}" }
        try {
            val url = repository.repositoryPath
            val repositoryBranch = getBranchOrDefault(branch)
            val progressMonitor = GitTaskProgressMonitor(url)

            val clone = Git.cloneRepository()
                    .setCloneAllBranches(false)
                    .setURI(url)
                    .setDirectory(path.toFile())
                    .setBranch(repositoryBranch)
                    .addAuthData(repository)
                    .setProgressMonitor(progressMonitor)
            return clone.call()
        } catch (e: GitAPIException) {
            log.error({ "There was a problem while a git clone operation for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch}" }, e)
            throw ConDocException("There was an error while clonning of repository. " + e)
        }
    }

    fun changeBranch(dir: Path, branch: String): Git {
        log.debug { "Changing a branch on ${dir}" }
        val git = dir.toGit()
        val trueBranch = getBranchOrDefault(branch)

        val headsRef: Ref? = git.repository.findRef("refs/heads/$trueBranch")
        val ref: Ref? = git.repository.findRef(trueBranch)

        if (headsRef == null) {

            if (ref?.name?.startsWith("refs/tags/") == true) {

                log.debug { "Branch does not exist on disk yet for RepositoryTag=$branch" }
                git.checkout()
                        .setCreateBranch(true)
                        .setName(trueBranch)
                        .setStartPoint("refs/tags/$trueBranch")
                        .call();

            } else {

                log.debug { "Branch does not exist on disk yet for RepositoryBranch=$branch" }
                git.branchCreate()
                        .setName(trueBranch)
                        .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                        .setStartPoint("origin/$trueBranch")
                        .setForce(true)
                        .call()

            }
        }

        git.checkout().setName("refs/heads/$trueBranch").setForced(true).call()
        log.debug { "Changing of a branch has been done for ${dir}" }
        return git
    }

    private fun getBranchOrDefault(branch: String): String {
        return if (StringUtils.isEmpty(branch)) "master" else branch
    }

    private fun getAllBranchesAndTags(operationResult: OperationResult): List<String> {
        return getAllBranchesAndTags(operationResult.advertisedRefs)
    }

    private fun getAllBranchesAndTags(refs: Collection<Ref>): List<String> {
        return getAllBranches(refs) + getAllTags(refs)
    }

    private fun parseLastCommitForBranch(refs: OperationResult, repositoryBranch: String): String {
        return getBranchOrTag(refs, repositoryBranch)!!.objectId.name
    }

    private fun getAllBranches(refs: Collection<Ref>): List<String> {
        return refs
                .map { it.name }
                .filter { ref -> ref.startsWith("refs/heads/") }
                .map { ref -> StringUtils.removeStart(ref, "refs/heads/") }
    }

    private fun getAllTags(refs: Collection<Ref>): List<String> {
        return refs
                .map { it.name }
                .filter { ref -> ref.startsWith("refs/tags/") }
                .map { ref -> StringUtils.removeStart(ref, "refs/tags/") }
    }

    private fun getBranchOrTag(refs: OperationResult, name: String): Ref? {
        return refs.advertisedRefs.find { it.name == "refs/heads/$name" || it.name == "refs/tags/$name" }
    }

    private fun Path.toGit() = Git.open(this.toFile())

    override fun getCommits(repository: Repository, branch: String, file: String): List<CommitInfo> {
        log.debug { "Will get all commits for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch}" }
        val git = cache.lock(repository, branch)
        try {
            val commits = git.log().addPath(file).call()
            log.debug { "Getting all commits has been done for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch}" }
            return commits.map { commit -> CommitInfo(commit.id.name, commit.authorIdent.name, commit.fullMessage, TimeUnit.SECONDS.toMillis(commit.commitTime.toLong())) }
        } catch (e: Exception) {
            throwRepositoryError(repository, e)
        } finally {
            cache.unlock(repository)
        }
    }

    override fun updateFile(repository: Repository, branch: String, file: String, newContent: String, commit: Commit) {
        log.debug { "Will update a file ${file} for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch}" }
        val git = cache.lock(repository, branch)
        try {
            val fileObj = Paths.get(git.repository.workTree.absolutePath).resolve(file).toFile()
            val parent = git.repository.workTree

            if (!fileObj.absolutePath.startsWith(parent.absolutePath)) {
                throw RuntimeException(FILE_WRITE_ATTEMPT)
            }

            fileObj.writeText(newContent)
            git.add().addFilepattern(file).call()
            git.commit().setAuthor(commit.user, commit.email).setMessage(commit.message).call()
        } catch (e: Exception) {
            throwRepositoryError(repository, e)
        } finally {
            cache.unlock(repository)
        }

    }

    override fun createNewBranch(repository: Repository, originBranch: String, newBranch: String) {
        log.debug { "Will create a new branch for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${newBranch}" }
        val git = cache.lock(repository, originBranch)
        //getLocalBranches will cause deadlock
        try {
            if (getAllBranches(git.branchList().call()).contains(newBranch)) {
                return
            }
            git.branchCreate().setName(newBranch).call()
            log.debug { "Creating of a branch has been done for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${newBranch}" }
        } catch (e: Exception) {
            throwRepositoryError(repository, e)
        } finally {
            cache.unlock(repository)
        }
    }

    override fun getLocalBranches(repository: Repository): List<String> {
        log.debug { "Getting a list of local branches for RepositoryPath=${repository.repositoryPath}" }
        val git = cache.lock(repository, "master")
        try {
            return getAllBranches(git.branchList().call())
        } catch (e: Exception) {
            throwRepositoryError(repository, e)
        } finally {
            cache.unlock(repository)
        }
    }

    override fun removeLocalBranch(repository: Repository, branch: String) {
        log.debug { "Will remove a local branch for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch}" }
        val git = cache.lock(repository, "master")
        try {
            git.branchDelete().setBranchNames(branch).setForce(true).call()
        } catch (e: Exception) {
            throwRepositoryError(repository, e)
        } finally {
            cache.unlock(repository)
        }
    }

    override fun getLocation(repository: Repository): File {
        log.debug { "Getting a location for RepositoryPath=${repository.repositoryPath}" }
        val git = cache.lock(repository, "master")
        val location = git.repository.directory.parentFile
        cache.unlock(repository)
        log.debug("Location for RepositoryPath=${repository.repositoryPath} is Dir=${location}")
        return location
    }

    override fun resetBranch(repository: Repository, branch: String) {
        val git = cache.lock(repository, branch)
        log.debug { "Resetting a branch for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch}" }
        try {
            git.reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/$branch").call()
        } catch (e: Exception) {
            throwRepositoryError(repository, e)
        } finally {
            cache.unlock(repository)
        }

    }

    override fun pushLocalBranch(repository: Repository, branch: String) {
        log.debug { "Pushing a local branch into remote for RepositoryPath=${repository.repositoryPath} on RepositoryBranch=${branch}" }
        val git = cache.lock(repository, branch)
        try {
            val push = git.push().addAuthData(repository).setRefSpecs(RefSpec("$branch:$branch")).call().first()

            //Related to: https://bugs.eclipse.org/bugs/show_bug.cgi?id=478199
            val pushStatus = push.remoteUpdates.first().status
            if (pushStatus != RemoteRefUpdate.Status.OK && pushStatus != RemoteRefUpdate.Status.UP_TO_DATE) {
                throw VerificationException(VerificationInfo(WRONG_CREDENTIALS))
            }
        } catch (e: Exception) {
            throwRepositoryError(repository, e)
        } finally {
            cache.unlock(repository)
        }
    }


    class GitTaskProgressMonitor(val repositoryPath: String) : BatchingProgressMonitor() {

        private val log = getLogger()

        override fun onUpdate(taskName: String?, workCurr: Int) {
            log.debug { "GIT: Task - ${taskName} - step ${workCurr} - ${repositoryPath}" }
        }

        override fun onUpdate(taskName: String?, workCurr: Int, workTotal: Int, percentDone: Int) {
            log.debug { "GIT: Task - ${taskName} - step ${workCurr} from total ${workTotal} - ${percentDone}% - ${repositoryPath}" }
        }

        override fun onEndTask(taskName: String?, workCurr: Int) {
            log.debug { "GIT: Finished task - ${taskName} - step ${workCurr} - ${repositoryPath}" }
        }

        override fun onEndTask(taskName: String?, workCurr: Int, workTotal: Int, percentDone: Int) {
            log.debug { "GIT: Finished Task - ${taskName} - step ${workCurr} from total ${workTotal} - ${percentDone}% - ${repositoryPath}" }
        }
    }

}
