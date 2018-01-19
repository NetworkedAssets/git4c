package com.networkedassets.git4c.core.datastore.cache

import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.bussiness.Cache

interface RefreshLocationUseCaseCache: Cache<Computation<Unit>> {
}