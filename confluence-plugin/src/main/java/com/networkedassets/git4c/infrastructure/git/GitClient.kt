package com.networkedassets.git4c.infrastructure.git

import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.core.bussiness.ImportedFiles
import com.networkedassets.git4c.core.bussiness.Revision
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.data.Repository

interface GitClient {

    @Throws(VerificationException::class)
    fun revision(repository: Repository, branch: String): Revision

    fun verify(repository: Repository): VerificationInfo

    fun getBranches(repository: Repository): List<String>

    fun pull(repository: Repository, branch: String): ImportedFiles
}
