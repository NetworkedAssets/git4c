package com.networkedassets.git4c.infrastructure.mocks.core.cache

import com.networkedassets.git4c.core.datastore.cache.RepositoryRevisionCache
import com.networkedassets.git4c.infrastructure.mocks.generic.InMemoryCache

class InMemoryRevisionCache : RepositoryRevisionCache, InMemoryCache<String>() {
    override fun putCached(repositoryPath: String, branch: String) {
        put(repositoryPath + "_" + branch, "");
    }

    override fun exists(repositoryPath: String, branch: String): Boolean {
        return isAvailable(repositoryPath + "_" + branch)
    }
}