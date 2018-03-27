package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest

class GetPredefinedRepositoryBranchesQuery(
        val predefinedRepository: String
) : AsyncBackendRequest()