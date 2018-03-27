package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.RepositoryToVerify
import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest

class VerifyRepositoryCommand(
        val repositoryToVerify: RepositoryToVerify
) : AsyncBackendRequest()