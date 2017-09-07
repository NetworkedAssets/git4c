package com.networkedassets.git4c.core.datastore.encryptors

interface Encryptor<DECRYPTED, ENCRYPTED> {

    fun encrypt(decrypted: DECRYPTED): ENCRYPTED

    fun decrypt(encrypted: ENCRYPTED): DECRYPTED

}