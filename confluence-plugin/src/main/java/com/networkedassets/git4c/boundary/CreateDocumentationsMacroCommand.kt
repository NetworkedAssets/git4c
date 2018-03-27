package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.DocumentationMacro
import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest


class CreateDocumentationsMacroCommand(
        val documentMacroMacroToCreate: DocumentationMacro,
        val user: String
) : AsyncBackendRequest()