package com.networkedassets.git4c.core.datastore.cache

import com.networkedassets.git4c.boundary.outbound.TemporaryBranch
import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.bussiness.Cache

interface TemporaryEditBranchResultCache : Cache<Computation<TemporaryBranch>>