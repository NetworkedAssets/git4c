package com.networkedassets.git4c.infrastructure.mocks.core.cache

import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.data.macro.documents.DocumentationsMacro
import com.networkedassets.git4c.infrastructure.mocks.generic.InMemoryCache

internal class InMemoryDocumentationsCache : DocumentsViewCache, InMemoryCache<DocumentationsMacro>()
