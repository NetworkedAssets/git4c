package com.networkedassets.git4c.infrastructure.mocks.core.cache

import com.networkedassets.git4c.core.datastore.cache.DocumentItemCache
import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem
import com.networkedassets.git4c.infrastructure.mocks.generic.InMemoryCache

class InMemoryDocumentatItemCache : DocumentItemCache, InMemoryCache<DocumentsItem>()
