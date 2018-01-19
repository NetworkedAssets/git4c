package com.networkedassets.git4c.core.business

interface UserManager {
    fun getUser(username: String): User?
}