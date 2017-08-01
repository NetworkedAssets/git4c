package com.networkedassets.git4c.core.common


interface CredentialsEncryptor {
    fun encrypt(password: String, key: String): String
    fun decrypt(password: String, key: String): String
}
