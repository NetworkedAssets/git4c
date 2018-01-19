package com.networkedassets.git4c.infrastructure.cache

import com.atlassian.cache.CacheFactory
import com.networkedassets.git4c.core.datastore.cache.DocumentItemCache
import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem

class AtlassianDocumentItemCache(cacheFactory: CacheFactory) : LongConfluenceCache<DocumentsItem>(cacheFactory, 100000), DocumentItemCache