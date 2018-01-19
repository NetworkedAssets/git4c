package com.networkedassets.git4c.core.bussiness

import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.core.business.Commit
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.data.CommitInfo
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.Repository
import java.io.File

interface SourcePlugin : Plugin {

    fun pull(repository: Repository, branch: String, alreadyLocked: Boolean = false): ImportedFiles

    fun get(repository: Repository, branch: String, alreadyLocked: Boolean = false): ImportedFiles

    fun getBranches(repository: Repository?): List<String>

    fun verify(repository: Repository?): VerificationInfo

    @Throws(VerificationException::class)
    fun revision(macroSettings: MacroSettings, repository: Repository?, alreadyLocked: Boolean = false): Revision


    fun getCommitsForFile(repository: Repository, branch: String, file: String): List<CommitInfo>

    //Local only
    fun updateFile(repository: Repository, branch: String, file: String, newContent: String, commit: Commit)

    //Local only
    //Can be used when branch already exists
    fun createNewBranch(repository: Repository, originBranch: String, newBranch: String)

    fun pushLocalBranch(repository: Repository, branch: String)

    //Local only
    fun resetBranch(repository: Repository, branch: String)

    fun isBranchMerged(repository: Repository, branch: String, base: String): Boolean

    //Used mostly for testing
    fun getLocalBranches(repository: Repository): List<String>

    fun removeBranch(repository: Repository, branchName: String)

    fun getLocation(repository: Repository): File

    fun isLocked(repositoryPath: String): Boolean

    fun accuireLock(repositoryPath: String): Boolean
}
