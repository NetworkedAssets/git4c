package com.networkedassets.git4c.core.bussiness

interface Parser {
    fun getMethods(content: String): List<Method>
}