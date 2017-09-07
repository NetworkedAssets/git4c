package com.networkedassets.git4c.core.bussiness

import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.Repository

interface SourcePlugin : Plugin {

    fun pull(repository: Repository, branch: String): ImportedFiles

    fun getBranches(repository: Repository?): List<String>

    fun verify(repository: Repository?): VerificationInfo

    @Throws(VerificationException::class)
    fun revision(macroSettings: MacroSettings, repository: Repository?): Revision
}
