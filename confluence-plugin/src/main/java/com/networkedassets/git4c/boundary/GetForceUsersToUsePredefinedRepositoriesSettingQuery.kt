package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.isForcedPredefined
import com.networkedassets.git4c.delivery.executor.result.BackendRequest


class GetForceUsersToUsePredefinedRepositoriesSettingQuery(
): BackendRequest<isForcedPredefined>()