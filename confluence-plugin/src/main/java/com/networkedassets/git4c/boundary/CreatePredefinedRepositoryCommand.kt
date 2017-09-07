package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.PredefinedRepository
import com.networkedassets.git4c.boundary.outbound.SavedPredefinedRepository
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class CreatePredefinedRepositoryCommand(
        val predefinedRepositoryToCreate: PredefinedRepository
) : BackendRequest<SavedPredefinedRepository>()