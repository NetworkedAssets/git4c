package com.networkedassets.git4c.core.bussiness

import com.atlassian.activeobjects.tx.Transactional

@Transactional
interface Database<T> {
    fun isAvailable(uuid: String): Boolean
    fun get(uuid: String): T?
    fun getAll(): List<T>
    fun put(uuid: String, data: T)
    fun remove(uuid: String)
    fun removeAll()
}