package com.networkedassets.git4c.core.business

interface PageManager {
    fun getAllPages(): List<Page>
    fun getAllPagesForSpace(spaceId: String): List<Page>
}