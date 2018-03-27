package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest

class GetTemporaryEditBranchCommand(
        val macroId: String
) : AsyncBackendRequest()