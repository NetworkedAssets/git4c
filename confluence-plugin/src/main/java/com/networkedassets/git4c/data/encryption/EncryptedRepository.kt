package com.networkedassets.git4c.data.encryption

import com.networkedassets.git4c.data.Repository

data class EncryptedRepository(
        val uuid: String,
        val repository: Repository,
        val securityKey: String
)