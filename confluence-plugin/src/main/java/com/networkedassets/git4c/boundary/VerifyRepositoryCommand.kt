package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.RepositoryToVerify
import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class VerifyRepositoryCommand (
        val repositoryToVerify: RepositoryToVerify
) : BackendRequest<VerificationInfo>()