package com.networkedassets.git4c.infrastructure.mocks.core.cache

import com.networkedassets.git4c.boundary.outbound.TemporaryBranch
import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.datastore.cache.TemporaryEditBranchResultCache
import com.networkedassets.git4c.infrastructure.mocks.generic.InMemoryCache

class InMemoryTemporaryEditBranchResultCache: InMemoryCache<Computation<TemporaryBranch>>(), TemporaryEditBranchResultCache