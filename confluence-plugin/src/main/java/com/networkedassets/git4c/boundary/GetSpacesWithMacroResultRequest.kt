package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.Spaces
import com.networkedassets.git4c.core.usecase.async.BackendRequestForAsyncResult

class GetSpacesWithMacroResultRequest(
        requestId: String
) : BackendRequestForAsyncResult<Spaces>(requestId)