package com.networkedassets.git4c.infrastructure

import com.networkedassets.git4c.core.bussiness.DataStore
import com.networkedassets.git4c.core.common.CredentialsEncryptor

abstract class Encryptor<Decrypted, Encrypted>(val dataStore: DataStore<Encrypted>, val encryptor: CredentialsEncryptor) : DataStore<Decrypted>

