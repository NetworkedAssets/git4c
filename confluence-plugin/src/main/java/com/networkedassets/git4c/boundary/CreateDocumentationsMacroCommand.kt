package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.DocumentationMacroToCreate
import com.networkedassets.git4c.boundary.outbound.CreatedDocumentationsMacro
import com.networkedassets.git4c.delivery.executor.result.BackendRequest


class CreateDocumentationsMacroCommand(
        val documentMacroMacroToCreate: DocumentationMacroToCreate
) : BackendRequest<CreatedDocumentationsMacro>()