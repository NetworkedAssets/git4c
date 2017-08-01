package com.networkedassets.git4c.infrastructure

import com.networkedassets.git4c.core.common.CredentialsEncryptor
import org.jasypt.util.text.BasicTextEncryptor;



class SimpleCredentialsEncryptor : CredentialsEncryptor{

    override fun encrypt(password: String, key: String): String {
        val encryptor = BasicTextEncryptor()
        encryptor.setPassword(key)
        return encryptor.encrypt(password)
    }

    override fun decrypt(password: String, key: String): String {
        val encryptor = BasicTextEncryptor()
        encryptor.setPassword(key)
        return encryptor.decrypt(password)

    }
}
