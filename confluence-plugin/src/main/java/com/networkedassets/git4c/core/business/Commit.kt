package com.networkedassets.git4c.core.business

data class Commit(
        val user: String,
        val email: String,
        val message: String
)