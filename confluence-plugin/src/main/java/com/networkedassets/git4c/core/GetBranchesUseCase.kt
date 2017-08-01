package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetBranchesQuery
import com.networkedassets.git4c.boundary.inbound.DocumentationToGetBranches
import com.networkedassets.git4c.boundary.outbound.Branches
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.data.macro.*
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class GetBranchesUseCase(val importer: SourcePlugin) : UseCase<GetBranchesQuery, Branches> {
    override fun execute(request: GetBranchesQuery): Result<Branches, Exception> {
        val credentials = detectCredentials(request.documentMacroToGetBranches)
        val macroSettings = DocumentationsMacroSettings("", request.documentMacroToGetBranches.sourceRepositoryUrl, credentials, "", "", "")
        val status = importer.verify(macroSettings)
        return if (status.isOk()) {
            Result.of { Branches(null, importer.getBranches(macroSettings).sorted()) }
        } else {
            Result.error(IllegalArgumentException(status.status.name))
        }
    }

    private fun detectCredentials(documentMacroToGetBranches: DocumentationToGetBranches) =
            when (documentMacroToGetBranches.credentials) {
                is inUserNamePassword -> UsernameAndPasswordCredentials(
                        documentMacroToGetBranches.credentials.username,
                        documentMacroToGetBranches.credentials.password)
                is inSshKey -> SshKeyCredentials(documentMacroToGetBranches.credentials.sshKey)
                is inNoAuth -> NoAuthCredentials()
                else -> throw RuntimeException("Unknown auth type: ${documentMacroToGetBranches.credentials.javaClass}")
            }

}