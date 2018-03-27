package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.RepositoryToGetBranches
import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest

class GetBranchesQuery(
        val repositoryToGetBranches: RepositoryToGetBranches
) : AsyncBackendRequest()
