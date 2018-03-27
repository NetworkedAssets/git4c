package com.networkedassets.git4c.infrastructure.cache

import com.atlassian.cache.CacheFactory
import com.networkedassets.git4c.core.datastore.cache.RepositoryRevisionCache
import java.util.*

class AtlassianRepositoryRevisionCache(cacheFactory: CacheFactory) : ShortConfluenceCache<Date>(cacheFactory), RepositoryRevisionCache {

    var timeToUse = 15000L

    override fun putCached(repositoryPath: String, branch: String) {
        put(repositoryPath + "_" + branch, Date());
    }

    override fun exists(repositoryPath: String, branch: String): Boolean {
        val isAvailabile = isAvailable(repositoryPath + "_" + branch)
        if (isAvailabile) {
            val date = get(repositoryPath + "_" + branch) ?: return false
            if (date.time > (System.currentTimeMillis() - timeToUse)) {
                return true
            } else {
                remove(repositoryPath + "_" + branch)
                return false
            }
        }
        return false
    }

    override fun setTime(time: Long) {
        this.timeToUse = time
    }

}