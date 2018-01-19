package com.networkedassets.git4c.infrastructure.plugin.source.git

import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.core.business.Commit
import com.networkedassets.git4c.core.bussiness.ImportedFiles
import com.networkedassets.git4c.core.bussiness.Revision
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.data.*
import com.networkedassets.git4c.infrastructure.git.GitClient
import java.io.File

class GitSourcePlugin(
        private val gitClient: GitClient
) : SourcePlugin {

    override val identifier: String get() = "git-source"

    override fun pull(repository: Repository, branch: String, alreadyLocked: Boolean): ImportedFiles {
        return gitClient.pull(repository, branch, alreadyLocked)
    }

    override fun get(repository: Repository, branch: String, alreadyLocked: Boolean): ImportedFiles {
        return gitClient.get(repository, branch, alreadyLocked)
    }

    @Throws(VerificationException::class)
    override fun revision(macroSettings: MacroSettings, repository: Repository?, alreadyLocked: Boolean): Revision {
        if (macroSettings.repositoryUuid == null) throw VerificationException(VerificationInfo(VerificationStatus.REMOVED))
        if (repository == null) throw VerificationException(VerificationInfo(VerificationStatus.REMOVED))
        if (!verifyUrl(repository)) throw VerificationException(VerificationInfo(VerificationStatus.WRONG_URL))
        val authDataVerifyStatus = verifyAuthData(repository)
        if (!authDataVerifyStatus.isOk()) {
            throw VerificationException(authDataVerifyStatus)
        }
        return gitClient.revision(repository, macroSettings.branch, alreadyLocked)
    }

    override fun verify(repository: Repository?): VerificationInfo {
        if (repository == null) return VerificationInfo(VerificationStatus.REMOVED)
        if (!verifyUrl(repository)) {
            return VerificationInfo(VerificationStatus.WRONG_URL)
        }

        val authDataVerifyStatus = verifyAuthData(repository)
        if (!authDataVerifyStatus.isOk()) {
            return authDataVerifyStatus
        }
        return gitClient.verify(repository)
    }

    override fun getBranches(repository: Repository?): List<String> {
        if (repository == null) return ArrayList()
        return gitClient.getBranches(repository)
    }

    override fun getCommitsForFile(repository: Repository, branch: String, file: String): List<CommitInfo> {
        return gitClient.getCommits(repository, branch, file)
    }

    override fun updateFile(repository: Repository, branch: String, file: String, newContent: String, commit: Commit) {
        gitClient.updateFile(repository, branch, file, newContent, commit)
    }

    override fun createNewBranch(repository: Repository, originBranch: String, newBranch: String) {
        return gitClient.createNewBranch(repository, originBranch, newBranch)
    }

    override fun pushLocalBranch(repository: Repository, branch: String) {
        gitClient.pushLocalBranch(repository, branch)
    }

    override fun resetBranch(repository: Repository, branch: String) {
        gitClient.resetBranch(repository, branch)
    }

    override fun isBranchMerged(repository: Repository, branch: String, base: String): Boolean {
        return gitClient.isBranchMerged(repository, branch, base)
    }

    override fun removeBranch(repository: Repository, branchName: String) {
        gitClient.removeLocalBranch(repository, branchName)
    }

    override fun getLocalBranches(repository: Repository): List<String> {
        return gitClient.getLocalBranches(repository)
    }

    override fun getLocation(repository: Repository): File {
        return gitClient.getLocation(repository)
    }

    override fun isLocked(repositoryPath: String): Boolean {
        return gitClient.isLocked(repositoryPath)
    }

    override fun accuireLock(repositoryPath: String): Boolean {
        return gitClient.accuireLock(repositoryPath)
    }

    private fun verifyAuthData(authType: Repository?): VerificationInfo {
        if (authType is RepositoryWithSshKey) {
            val sshKey = authType.sshKey.trim()
            //Key cannot have "ENCRYPTED" in it
            if (!sshKey.startsWith("-----BEGIN RSA PRIVATE KEY-----")
                    || !sshKey.endsWith("-----END RSA PRIVATE KEY-----")
                    || sshKey.contains("ENCRYPTED")) {
                return VerificationInfo(VerificationStatus.WRONG_KEY_FORMAT)
            }
        }
        return VerificationInfo(VerificationStatus.OK)
    }

    private fun verifyUrl(repository: Repository): Boolean {
        when (repository) {
            is RepositoryWithSshKey -> {
                if (repository.repositoryPath.startsWith("http")) {
                    return false
                }
            }
            is RepositoryWithUsernameAndPassword -> {
                if (repository.repositoryPath.startsWith("ssh")) {
                    return false
                }
            }
            is RepositoryWithNoAuthorization -> {
                if (repository.repositoryPath.startsWith("ssh")) {
                    return false
                }
            }
        }
        return true
    }

}
