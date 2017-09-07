package com.networkedassets.git4c.infrastructure.cache

import com.atlassian.cache.CacheFactory
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.data.macro.documents.DocumentationsMacro

class AtlassianDocumentsViewCache(cacheFactory: CacheFactory) : ConfluenceCache<DocumentationsMacro>(cacheFactory), DocumentsViewCache