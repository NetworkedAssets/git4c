package com.networkedassets.git4c.core.datastore.cache

import com.networkedassets.git4c.boundary.outbound.Spaces
import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.bussiness.Cache

interface SpacesWithMacroResultCache : Cache<Computation<Spaces>>