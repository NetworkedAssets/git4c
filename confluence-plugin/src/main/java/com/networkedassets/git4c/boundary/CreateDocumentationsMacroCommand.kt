package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.DocumentationMacro
import com.networkedassets.git4c.boundary.outbound.SavedDocumentationsMacro
import com.networkedassets.git4c.delivery.executor.result.BackendRequest


class CreateDocumentationsMacroCommand(
        val documentMacroMacroToCreate: DocumentationMacro
) : BackendRequest<SavedDocumentationsMacro>()