package com.networkedassets.git4c.infrastructure.cache

import com.atlassian.cache.CacheFactory
import com.networkedassets.git4c.core.datastore.cache.DocumentToBeConvertedLockCache
import com.networkedassets.git4c.data.DocumentView


class AtlassianDocumentConvertionCache(cacheFactory: CacheFactory) : ShortConfluenceCache<DocumentView>(cacheFactory), DocumentToBeConvertedLockCache
