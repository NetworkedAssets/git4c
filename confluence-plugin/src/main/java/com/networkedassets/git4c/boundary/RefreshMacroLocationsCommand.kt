package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.RequestId
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class RefreshMacroLocationsCommand(
): BackendRequest<RequestId>()