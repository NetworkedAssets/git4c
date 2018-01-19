package com.networkedassets.git4c.infrastructure.git

import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.core.business.Commit
import com.networkedassets.git4c.core.bussiness.ImportedFiles
import com.networkedassets.git4c.core.bussiness.Revision
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.data.CommitInfo
import com.networkedassets.git4c.data.Repository
import java.io.File

interface GitClient {

    @Throws(VerificationException::class)
    fun revision(repository: Repository, branch: String, alreadyLocked : Boolean): Revision

    fun verify(repository: Repository): VerificationInfo

    fun getBranches(repository: Repository): List<String>

    fun pull(repository: Repository, branch: String, alreadyLocked: Boolean): ImportedFiles

    fun get(repository: Repository, branch: String, alreadyLocked: Boolean): ImportedFiles

    fun getCommits(repository: Repository, branch: String, file: String): List<CommitInfo>

    fun updateFile(repository: Repository, branch: String, file: String, newContent: String, commit: Commit)

    fun createNewBranch(repository: Repository, originBranch: String, newBranch: String)

    fun getLocation(repository: Repository): File

    fun removeLocalBranch(repository: Repository, branch: String)

    fun getLocalBranches(repository: Repository): List<String>

    fun pushLocalBranch(repository: Repository, branch: String)

    fun resetBranch(repository: Repository, branch: String)

    fun isLocked(repositoryPath: String): Boolean

    fun accuireLock(repositoryPath: String): Boolean

    fun isBranchMerged(repository: Repository, branch: String, origin: String): Boolean

}
