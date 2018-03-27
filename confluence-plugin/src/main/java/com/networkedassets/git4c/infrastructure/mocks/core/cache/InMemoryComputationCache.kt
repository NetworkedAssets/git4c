package com.networkedassets.git4c.infrastructure.mocks.core.cache

import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.infrastructure.mocks.generic.InMemoryCache

class InMemoryComputationCache<T>() : ComputationCache<T>, InMemoryCache<Computation<T>>()