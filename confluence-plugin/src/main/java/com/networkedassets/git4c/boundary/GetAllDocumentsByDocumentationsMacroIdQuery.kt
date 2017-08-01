package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetAllDocumentsByDocumentationsMacroIdQuery(
        val macroId: String
): BackendRequest<List<DocumentsItem>>()