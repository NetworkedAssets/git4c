package com.networkedassets.git4c.core.bussiness

interface ContentFilter {
    fun filter(content: String): String
}