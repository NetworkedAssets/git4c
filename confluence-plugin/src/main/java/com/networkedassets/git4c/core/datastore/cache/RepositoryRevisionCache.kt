package com.networkedassets.git4c.core.datastore.cache

import com.networkedassets.git4c.core.bussiness.Cache
import java.util.*

interface RepositoryRevisionCache : Cache<Date> {
    fun exists(repositoryPath: String, branch: String): Boolean
    fun putCached(repositoryPath: String, branch: String)
    fun setTime(time : Long)
}