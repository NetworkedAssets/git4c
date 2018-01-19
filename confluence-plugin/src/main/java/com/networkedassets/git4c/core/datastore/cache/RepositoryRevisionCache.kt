package com.networkedassets.git4c.core.datastore.cache

import com.networkedassets.git4c.core.bussiness.Cache

interface RepositoryRevisionCache : Cache<String> {
    fun exists(repositoryPath: String, branch: String): Boolean
    fun putCached(repositoryPath: String, branch: String)
}