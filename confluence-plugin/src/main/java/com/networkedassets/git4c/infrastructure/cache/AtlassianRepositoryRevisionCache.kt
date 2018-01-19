package com.networkedassets.git4c.infrastructure.cache

import com.atlassian.cache.CacheFactory
import com.networkedassets.git4c.core.datastore.cache.RepositoryRevisionCache

class AtlassianRepositoryRevisionCache(cacheFactory: CacheFactory) : MinuteConfluenceCache<String>(cacheFactory), RepositoryRevisionCache {

    override fun putCached(repositoryPath: String, branch: String) {
        put(repositoryPath + "_" + branch, "");
    }

    override fun exists(repositoryPath: String, branch: String): Boolean {
        return isAvailable(repositoryPath + "_" + branch)
    }
}