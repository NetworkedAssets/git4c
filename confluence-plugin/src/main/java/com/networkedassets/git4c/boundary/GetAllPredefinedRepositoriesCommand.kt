package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.PredefinedRepository
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetAllPredefinedRepositoriesCommand
    : BackendRequest<List<PredefinedRepository>>()