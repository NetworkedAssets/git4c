package com.networkedassets.git4c.infrastructure.git

import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.boundary.outbound.VerificationStatus.*
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.core.exceptions.ConDocException
import com.networkedassets.git4c.data.macro.*
import com.networkedassets.git4c.utils.ThrowableUtils.isCausedBy
import com.networkedassets.git4c.infrastructure.plugin.source.git.ImportedSourceFile
import org.apache.commons.lang3.StringUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.LsRemoteCommand
import org.eclipse.jgit.api.TransportCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.internal.JGitText
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.util.FS
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path
import java.util.*
import java.util.Collections.emptySet
import java.util.stream.Collectors
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode

class DefaultGitClient : GitClient {

    private val log = LoggerFactory.getLogger(DefaultGitClient::class.java)

    override fun verify(documentationsMacroSettings: DocumentationsMacroSettings): VerificationInfo {

        if (!verifyUrl(documentationsMacroSettings)) {
            return VerificationInfo(WRONG_URL)
        }

        val authDataVerifyStatus = verifyAuthData(documentationsMacroSettings.credentials)
        if (!authDataVerifyStatus.isOk()) {
            return authDataVerifyStatus
        }

        val connectionStatus = verifyConnection(documentationsMacroSettings)
        if (!connectionStatus.isOk()) {
            return connectionStatus
        }

        val branchStatus = verifyBranch(documentationsMacroSettings)
        if (!branchStatus.isOk()) {
            return branchStatus
        }

        return VerificationInfo(OK)

    }

    private fun verifyBranch(documentationsMacroSettings: DocumentationsMacroSettings): VerificationInfo {

        val url = documentationsMacroSettings.repositoryPath
        val branch = getBranch(documentationsMacroSettings.branch)
        val lsCmd = LsRemoteCommand(null)
        addAuthData(lsCmd, documentationsMacroSettings.credentials)
                .setRemote(url)
        val refs = lsCmd.call()
        val revs = refs.stream()
                .map<String>({ it.name })
                .filter { ref -> ref.startsWith("refs/heads/") }
                .map { ref -> StringUtils.removeStart(ref, "refs/heads/") }
                .collect(Collectors.toList<String>())

        if (!revs.contains(branch)) {
            return VerificationInfo(WRONG_BRANCH)
        } else
            return VerificationInfo(OK)
    }

    private fun verifyAuthData(authType: RepositoryAuthorization): VerificationInfo {
        if (authType is SshKeyCredentials) {
            val sshKey = authType.sshKey.trim()
            //Key cannot have "ENCRYPTED" in it
            if (!sshKey.startsWith("-----BEGIN RSA PRIVATE KEY-----")
                    || !sshKey.endsWith("-----END RSA PRIVATE KEY-----")
                    || sshKey.contains("ENCRYPTED")) {
                return VerificationInfo(WRONG_KEY_FORMAT)
            }
        }
        return VerificationInfo(OK)
    }

    private fun verifyConnection(documentationsMacroSettings: DocumentationsMacroSettings): VerificationInfo {
        val url = documentationsMacroSettings.repositoryPath
        val lsCmd = LsRemoteCommand(null)
        addAuthData(lsCmd, documentationsMacroSettings.credentials)
                .setRemote(url)
        try {
            lsCmd.call()
        } catch (e: GitAPIException) {

            log.error("Problem with get repository", e)

            if (e.message?.contains(JGitText.get().notAuthorized) ?: false) {
                return VerificationInfo(WRONG_CREDENTIALS)
            }

            if (e.message?.contains("Auth fail") ?: false) {
                return VerificationInfo(WRONG_CREDENTIALS)
            }

            if (e.message?.contains(JGitText.get().notFound) ?: false) {
                return VerificationInfo(SOURCE_NOT_FOUND)
            }

            if (e.message?.contains("CAPTCHA") ?: false) {
                return VerificationInfo(CAPTCHA_REQUIRED)
            }

            if (e.isCausedBy(JSchException::class) && e.message.orEmpty().contains("invalid privatekey")) {
                return VerificationInfo(WRONG_KEY_FORMAT)
            }

            return VerificationInfo(SOURCE_NOT_FOUND)
        }
        return VerificationInfo(OK)
    }

    override fun verify(documentationsMacroSettings: ShortDocumentationsMacroSettings): VerificationInfo {
        val settings = DocumentationsMacroSettings("", documentationsMacroSettings.repositoryPath, documentationsMacroSettings.credentials, "", "", "")
        if (!verifyUrl(settings)) {
            return VerificationInfo(WRONG_URL)
        }

        val authDataVerifyStatus = verifyAuthData(documentationsMacroSettings.credentials)
        if (!authDataVerifyStatus.isOk()) {
            return authDataVerifyStatus
        }

        return verifyConnection(settings)
    }


    private fun verifyUrl(documentationsMacroSettings: DocumentationsMacroSettings): Boolean {
        val credentials = documentationsMacroSettings.credentials

        when (credentials) {
            is SshKeyCredentials -> {
                if (documentationsMacroSettings.repositoryPath.startsWith("http")) {
                    return false
                }
            }
            is UsernameAndPasswordCredentials -> {
                if (documentationsMacroSettings.repositoryPath.startsWith("ssh")) {
                    return false
                }
            }
            is NoAuthCredentials -> {
                if (documentationsMacroSettings.repositoryPath.startsWith("ssh")) {
                    return false
                }

            }
        }

        return true
    }


    private fun <T : TransportCommand<*, *>> addAuthData(command: T, authType: RepositoryAuthorization): T {
        if (authType is UsernameAndPasswordCredentials) {
            val auth = authType
            val provider = UsernamePasswordCredentialsProvider(auth.username, auth.password)
            command.setCredentialsProvider(provider)
            return command
        } else if (authType is SshKeyCredentials) {
            prepareSshKeyTransport(command, authType)
            return command
        } else if (authType is NoAuthCredentials) {
            return command
        } else {
            throw RuntimeException("Unknown GitAuthType: " + authType.javaClass.toString())
        }
    }

    private fun <T : TransportCommand<*, *>> prepareSshKeyTransport(command: T, authType: SshKeyCredentials) {
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
        command.setTransportConfigCallback { transport ->
            val sshTransport = transport as SshTransport
            sshTransport.sshSessionFactory = sshSessionFactory
        }
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

    override fun getBranches(documentationsMacroSettings: DocumentationsMacroSettings): List<String> {

        val url = documentationsMacroSettings.repositoryPath
        val lsCmd = LsRemoteCommand(null)
        addAuthData(lsCmd, documentationsMacroSettings.credentials)
                .setRemote(url)

        return lsCmd.call()
                .map { it.name }
                .filter { ref -> ref.startsWith("refs/heads/") }
                .map { ref -> StringUtils.removeStart(ref, "refs/heads/") }

    }

    override fun fetchRawData(documentationsMacroSettings: DocumentationsMacroSettings, temp: Path): List<ImportedFileData> {

        val git = doClone(documentationsMacroSettings, temp)

        return getFilesFromDir(temp.toFile())
                .map { file -> ImportedSourceFile(git, temp.toFile(), file) }
                .filter { file -> !file.isInternalGitFile() }
                .map { it.convert() }

    }

    override fun revision(documentationsMacroSettings: DocumentationsMacroSettings): String {
        try {

            val url = documentationsMacroSettings.repositoryPath
            val branch = getBranch(documentationsMacroSettings.branch)

            val lsCmd = LsRemoteCommand(null)
            val commit = addAuthData(lsCmd, documentationsMacroSettings.credentials)
                    .setRemote(url).call()
                    .stream()
                    .filter { ref -> ref.name.startsWith("refs/heads/") }
                    .filter { ref -> StringUtils.removeStart(ref.name, "refs/heads/") == branch }
                    .map { ref -> ref.objectId.name }
                    .collect(Collectors.toList<String>())[0]

            return commit
        } catch (e: Exception) {
            throw ConDocException("There was an error while cloning the repository.. " + e)
        }

    }

    override fun changeBranch(dir: Path, documentationsMacroSettings: DocumentationsMacroSettings) {
        val git = dir.toGit()
        val trueBranch = getBranch(documentationsMacroSettings.branch)

        val branchExists = git.repository.findRef(trueBranch) != null
        if (!branchExists) {
            git.branchCreate()
                    .setName(trueBranch)
                    .setUpstreamMode(SetupUpstreamMode.TRACK)
                    .setStartPoint("origin/" + trueBranch)
                    .call()
        }

        git.checkout().setName(trueBranch).call()

        val pull = git.pull()
        addAuthData(pull, documentationsMacroSettings.credentials)
        pull.call()
    }

    override fun fetchRawData(path: Path): List<ImportedFileData> {
        val git = path.toGit()
        val temp = path

        return getFilesFromDir(temp.toFile())
                .map { file -> ImportedSourceFile(git, temp.toFile(), file) }
                .filter { file -> !file.isInternalGitFile() }
                .map { it.convert() }

    }

    fun getBranch(branch: String): String {
        return if (StringUtils.isEmpty(branch)) "master" else branch
    }

    override fun clone(documentationsMacroSettings: DocumentationsMacroSettings, path: Path) {
        doClone(documentationsMacroSettings, path)
    }

    private fun doClone(documentationsMacroSettings: DocumentationsMacroSettings, path: Path): Git {

        try {

            val url = documentationsMacroSettings.repositoryPath
            val branch = getBranch(documentationsMacroSettings.branch)

            val clone = Git.cloneRepository().setURI(url).setDirectory(path.toFile())
                    .setBranch(branch)
            addAuthData(clone, documentationsMacroSettings.credentials)
            return clone.call()

        } catch (e: GitAPIException) {
            throw ConDocException("There was an error while cloning the repository.. " + e)
        }

    }

    private fun Path.toGit() = Git.open(this.toFile())

}