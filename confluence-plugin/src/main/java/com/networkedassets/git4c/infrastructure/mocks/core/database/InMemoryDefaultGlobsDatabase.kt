package com.networkedassets.git4c.infrastructure.mocks.core.database

import com.networkedassets.git4c.core.datastore.repositories.PredefinedGlobsDatabase
import com.networkedassets.git4c.data.PredefinedGlob
import com.networkedassets.git4c.infrastructure.mocks.generic.InMemoryCache

class InMemoryDefaultGlobsDatabase : InMemoryCache<PredefinedGlob>(), PredefinedGlobsDatabase
