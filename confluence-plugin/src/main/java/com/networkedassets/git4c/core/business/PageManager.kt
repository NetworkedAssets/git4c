package com.networkedassets.git4c.core.business

interface PageManager {
    fun getAllPageKeys(): List<Long>
    fun getAllPagesKeysForSpace(spaceKey: String): List<Long>
    fun getPage(pageId: Long): Page?
}