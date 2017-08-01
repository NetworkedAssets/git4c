package com.networkedassets.git4c.infrastructure

import com.atlassian.cache.CacheFactory
import com.networkedassets.git4c.core.datastore.DocumentsViewCache
import com.networkedassets.git4c.data.macro.documents.DocumentationsMacro

class AtlassianDocumentsViewCache(cacheFactory: CacheFactory) : ConfluenceCache<DocumentationsMacro>(cacheFactory), DocumentsViewCache