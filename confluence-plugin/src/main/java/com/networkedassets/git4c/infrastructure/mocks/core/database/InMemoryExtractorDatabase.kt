package com.networkedassets.git4c.infrastructure.mocks.core.database

import com.networkedassets.git4c.core.datastore.extractors.ExtractorData
import com.networkedassets.git4c.core.datastore.repositories.ExtractorDataDatabase
import com.networkedassets.git4c.infrastructure.mocks.generic.InMemoryCache

class InMemoryExtractorDatabase : InMemoryCache<ExtractorData>(), ExtractorDataDatabase
