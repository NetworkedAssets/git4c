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
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword
import com.networkedassets.git4c.infrastructure.plugin.source.git.ImportedSourceFile
import com.networkedassets.git4c.utils.ThrowableUtils.isCausedBy
import org.apache.commons.lang3.StringUtils
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.LsRemoteCommand
import org.eclipse.jgit.api.TransportCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.internal.JGitText
import org.eclipse.jgit.transport.*
import org.eclipse.jgit.util.FS
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.File
import java.nio.file.Path
import java.util.*
import java.util.Collections.emptySet
import java.util.stream.Collectors

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
            val revs = parseRevisions(refs)
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

    private fun parseRevisions(refs: FetchResult): List<String> {
        return refs.advertisedRefs
                .map({ it.name })
                .filter { ref -> ref.startsWith("refs/heads/") }
                .map { ref -> StringUtils.removeStart(ref, "refs/heads/") }
    }

    private fun parseLastCommitForBranch(refs: FetchResult, repositoryBranch: String): String {
        return refs.advertisedRefs
                .filter { ref -> ref.name.startsWith("refs/heads/") }
                .filter { ref -> StringUtils.removeStart(ref.name, "refs/heads/") == repositoryBranch }
                .map { ref -> ref.objectId.name }[0]
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
            //Git shouldn't have empty directories, but let's do this just to be sure
            val content = dir.listFiles()
            val safeContent = content ?: arrayOf<File>()

            //TODO: Ignore ".git/"?
            return Arrays.stream(safeContent)
                    .map<Collection<File>>({ this.getFilesFromDir(it) })
                    .flatMap<File>({ it.stream() })
                    .collect(Collectors.toSet<File>())

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
        return command.call()
                .map { it.name }
                .filter { ref -> ref.startsWith("refs/heads/") }
                .map { ref -> StringUtils.removeStart(ref, "refs/heads/") }
    }

    override fun pull(repository: Repository, branch: String): ImportedFiles {
        val git = cache.lock(repository, branch)
        try {
            git.pull().addAuthData(repository).call()
        } catch (e: Exception) {
            cache.unlock(repository)
            throw e
        }
        return ImportedFiles(
                getFilesFromDir(git.repository.workTree)
                        .map { file -> ImportedSourceFile(git, git.repository.workTree, file) }
                        .filter { file -> !file.isInternalGitFile() }
                        .map { it.convert() }, Closeable { cache.unlock(repository) }
        )
    }

    fun getBranchOrDefault(branch: String): String {
        return if (StringUtils.isEmpty(branch)) "master" else branch
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

    private fun Path.toGit() = Git.open(this.toFile())

}
