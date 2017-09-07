package com.networkedassets.git4c.core.datastore.repositories

import com.atlassian.activeobjects.tx.Transactional
import com.networkedassets.git4c.core.bussiness.Database
import com.networkedassets.git4c.data.encryption.EncryptedRepository

@Transactional
interface EncryptedRepositoryDatabase : Database<EncryptedRepository>
