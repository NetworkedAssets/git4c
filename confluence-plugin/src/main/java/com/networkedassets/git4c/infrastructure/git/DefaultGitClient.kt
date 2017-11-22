package com.networkedassets.git4c.infrastructure.git

import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.boundary.outbound.VerificationStatus.*
import com.networkedassets.git4c.core.bussiness.ImportedFiles
import com.networkedassets.git4c.core.bussiness.Revision
import com.networkedassets.git4c.core.exceptions.ConDocException
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.data.*
import com.networkedassets.git4c.infrastructure.plugin.source.git.ImportedSourceFile
import com.networkedassets.git4c.utils.ThrowableUtils.isCausedBy
import org.apache.commons.lang3.StringUtils
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.TransportCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.api.errors.NoHeadException
import org.eclipse.jgit.internal.JGitText
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.transport.*
import org.eclipse.jgit.util.FS
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.File
import java.nio.file.Path
import java.util.Collections.emptySet
import java.util.concurrent.TimeUnit

class DefaultGitClient : GitClient {

    private val log = LoggerFactory.getLogger(DefaultGitClient::class.java)

    private val cache = GitDiskCache(this)

    @Throws(VerificationException::class)
    override fun revision(repository: Repository, branch: String): Revision {
        val branchStatus = branchRevision(repository, branch)
        return branchStatus
    }

    @Throws(VerificationException::class)
    private fun branchRevision(repository: Repository, branch: String): Revision {
        val repositoryBranch = getBranchOrDefault(branch)
        val git = cache.lock(repository, branch)
        val command = git.fetch().addAuthData(repository)
        try {
            val refs = command.call()
            val revs = getAllBranchesAndTags(refs)
            val commit = parseLastCommitForBranch(refs, repositoryBranch)
            if (!revs.contains(repositoryBranch)) throw VerificationException(VerificationInfo(WRONG_BRANCH))
            return closeableRevision(commit, repository)
        } catch (e: Exception) {
            cache.unlock(repository)
            throwRepositoryError(e)
        }
        throw VerificationException(VerificationInfo(SOURCE_NOT_FOUND))
    }

    private fun closeableRevision(commit: String, repository: Repository): Revision {
        return Revision(commit, Closeable { cache.unlock(repository) })
    }

    @Throws(VerificationException::class)
    private fun throwRepositoryError(e: Exception) {
        log.error("Problem with repository: {}", e.message)

        if (e.message?.contains(JGitText.get().notAuthorized) ?: false) {
            throw VerificationException(VerificationInfo(WRONG_CREDENTIALS))
        }

        if (e.message?.contains("Auth fail") ?: false) {
            throw VerificationException(VerificationInfo(WRONG_CREDENTIALS))
        }

        if (e.message?.contains(JGitText.get().notFound) ?: false) {
            throw VerificationException(VerificationInfo(SOURCE_NOT_FOUND))
        }

        if (e.message?.contains("CAPTCHA") ?: false) {
            throw VerificationException(VerificationInfo(CAPTCHA_REQUIRED))
        }

        if (e.isCausedBy(JSchException::class) && e.message.orEmpty().contains("invalid privatekey")) {
            throw VerificationException(VerificationInfo(WRONG_KEY_FORMAT))
        }

        throw VerificationException(VerificationInfo(SOURCE_NOT_FOUND))
    }

    override fun verify(repository: Repository): VerificationInfo {
        val url = repository.repositoryPath
        try {
            Git.lsRemoteRepository().setRemote(url).addAuthData(repository).call()
        } catch (e: GitAPIException) {
            return getRepositoryError(e)
        }
        return VerificationInfo(OK)
    }

    private fun getRepositoryError(e: GitAPIException): VerificationInfo {
        try {
            throwRepositoryError(e)
            return VerificationInfo(SOURCE_NOT_FOUND)
        } catch (e: VerificationException) {
            return e.verification
        }
    }

    private fun <T : TransportCommand<*, *>> T.addAuthData(authType: Repository?): T {
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

    private fun getFilesFromDir(dir: File): Collection<File> {

        if (dir.isDirectory) {
            val content = dir.listFiles().filter { !it.name.contains(".git") }

            return content
                    .map({ this.getFilesFromDir(it) })
                    .flatMap({ it })

        } else if (dir.isFile) {
            return setOf(dir)
        } else {
            log.warn("Not a file and not a directory: {}", dir.absolutePath)
            return emptySet()
        }

    }

    override fun getBranches(repository: Repository): List<String> {
        val url = repository.repositoryPath
        val command = Git.lsRemoteRepository().addAuthData(repository)
                .setRemote(url)
        return getAllBranchesAndTags(command.call())
    }

    override fun pull(repository: Repository, branch: String): ImportedFiles {
       return  pull(repository, branch, 0)
    }

    private fun pull(repository: Repository, branch: String, retry : Int): ImportedFiles {
        val git = cache.lock(repository, branch)
        try {
            git.pull().addAuthData(repository).call()
        } catch (e: NoHeadException) {
            //Everything's fine - we're just trying to pull a tag which is not possible
        } catch (e: Exception) {
            cache.removeCache(repository)
            cache.unlock(repository)
            if (retry<3) return pull(repository, branch, retry+1) else throw e
        }
        val answers = getFilesFromDir(git.repository.workTree)
        val answer = answers
                .map { file -> ImportedSourceFile(git, git.repository.workTree, file) }
                .filter { file -> !file.isInternalGitFile() }
                .map { it.convert() }
        return ImportedFiles(answer, Closeable { cache.unlock(repository) })
    }

    override fun get(repository: Repository, branch: String): ImportedFiles {
        val git = cache.lock(repository, branch)
        val answers = getFilesFromDir(git.repository.workTree)
        val answer = answers
                .map { file -> ImportedSourceFile(git, git.repository.workTree, file) }
                .filter { file -> !file.isInternalGitFile() }
                .map { it.convert() }
        return ImportedFiles(answer, Closeable { cache.unlock(repository) })
    }


    fun clone(repository: Repository, branch: String, path: Path): Git {
        try {
            val url = repository.repositoryPath
            val repositoryBranch = getBranchOrDefault(branch)

            val clone = Git.cloneRepository().setURI(url).setDirectory(path.toFile())
                    .setBranch(repositoryBranch).addAuthData(repository)
            return clone.call()

        } catch (e: GitAPIException) {
            throw ConDocException("There was an error while clonning of repository. " + e)
        }
    }

    fun changeBranch(dir: Path, branch: String): Git {
        val git = dir.toGit()
        val trueBranch = getBranchOrDefault(branch)

        val branchExists = git.repository.findRef(trueBranch) != null
        if (!branchExists) {
            git.branchCreate()
                    .setName(trueBranch)
                    .setUpstreamMode(SetupUpstreamMode.TRACK)
                    .setStartPoint("origin/" + trueBranch)
                    .setForce(true)
                    .call()
        }

        git.checkout().setName(trueBranch).call()
        return git
    }

    fun getBranchOrDefault(branch: String): String {
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
                .map({ it.name })
                .filter { ref -> ref.startsWith("refs/heads/") }
                .map { ref -> StringUtils.removeStart(ref, "refs/heads/") }
    }

    private fun getAllTags(refs: Collection<Ref>): List<String> {
        return refs
                .map({ it.name })
                .filter { ref -> ref.startsWith("refs/tags/") }
                .map { ref -> StringUtils.removeStart(ref, "refs/tags/") }
    }

    private fun getBranchOrTag(refs: OperationResult, name: String): Ref? {
        return refs.advertisedRefs.find { it.name == "refs/heads/$name" || it.name == "refs/tags/$name" }
    }

    private fun Path.toGit() = Git.open(this.toFile())

    override fun getCommits(repository: Repository, branch: String, file: String): List<CommitInfo> {
        val git = cache.lock(repository, branch)
        try {
            val commits = git.log().addPath(file).call()
            return commits.map { commit -> CommitInfo(commit.id.name, commit.authorIdent.name, commit.fullMessage, TimeUnit.SECONDS.toMillis(commit.commitTime.toLong())) }
        } catch (e: Exception) {
            throwRepositoryError(e)
        } finally {
            cache.unlock(repository)
        }
        throw VerificationException(VerificationInfo(SOURCE_NOT_FOUND))
    }
}
