package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.RepositoryToGetBranches
import com.networkedassets.git4c.boundary.outbound.Branches
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetBranchesQuery(
        val repositoryToGetBranches: RepositoryToGetBranches
) : BackendRequest<Branches>()
