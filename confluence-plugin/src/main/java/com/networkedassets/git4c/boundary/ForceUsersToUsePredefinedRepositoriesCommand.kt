package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.ForcePredefinedRepositoriesInfo
import com.networkedassets.git4c.boundary.outbound.isForcedPredefined
import com.networkedassets.git4c.delivery.executor.result.BackendRequest


class ForceUsersToUsePredefinedRepositoriesCommand(
        val force: ForcePredefinedRepositoriesInfo
): BackendRequest<isForcedPredefined>()