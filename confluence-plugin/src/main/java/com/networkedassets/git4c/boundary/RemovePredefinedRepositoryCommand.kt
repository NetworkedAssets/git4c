package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class RemovePredefinedRepositoryCommand(
        val repositoryId: String
) : BackendRequest<String>()