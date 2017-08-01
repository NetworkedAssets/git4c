package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.DocumentationsMacro
import com.networkedassets.git4c.delivery.executor.result.BackendRequest


class RefreshDocumentationsMacroCommand(
        val macroId: String
) : BackendRequest<DocumentationsMacro>()