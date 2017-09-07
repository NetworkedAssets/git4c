package com.networkedassets.git4c.infrastructure.plugin.source.git

import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.core.bussiness.ImportedFiles
import com.networkedassets.git4c.core.bussiness.Revision
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.data.*
import com.networkedassets.git4c.infrastructure.git.GitClient

class GitSourcePlugin(
        private val gitClient: GitClient
) : SourcePlugin {

    override val identifier: String get() = "git-source"

    override fun pull(repository: Repository, branch: String): ImportedFiles {
        return gitClient.pull(repository, branch)
    }

    @Throws(VerificationException::class)
    override fun revision(macroSettings: MacroSettings, repository: Repository?): Revision {
        if (macroSettings.repositoryUuid == null) throw VerificationException(VerificationInfo(VerificationStatus.REMOVED))
        if (repository == null) throw VerificationException(VerificationInfo(VerificationStatus.REMOVED))
        if (!verifyUrl(repository)) throw VerificationException(VerificationInfo(VerificationStatus.WRONG_URL))
        val authDataVerifyStatus = verifyAuthData(repository)
        if (!authDataVerifyStatus.isOk()) {
            throw VerificationException(authDataVerifyStatus)
        }
        return gitClient.revision(repository, macroSettings.branch)
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
