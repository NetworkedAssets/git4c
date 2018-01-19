package com.networkedassets.git4c.infrastructure.mocks.core.database

import com.networkedassets.git4c.core.datastore.repositories.TemporaryEditBranchesDatabase
import com.networkedassets.git4c.data.TemporaryEditBranch
import com.networkedassets.git4c.infrastructure.mocks.generic.InMemoryCache

class InMemoryTemporaryEditBranchesDatabase: InMemoryCache<TemporaryEditBranch>(), TemporaryEditBranchesDatabase