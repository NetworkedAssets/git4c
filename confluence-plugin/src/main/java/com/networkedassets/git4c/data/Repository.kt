package com.networkedassets.git4c.data


abstract class Repository(
        val uuid: String,
        val repositoryPath: String
)

data class RepositoryWithUsernameAndPassword(
        private val uuidOfRepository: String,
        private val path: String,
        val username: String,
        val password: String
) : Repository(uuidOfRepository, path)

data class RepositoryWithSshKey(
        private val uuidOfRepository: String,
        private val path: String,
        val sshKey: String
) : Repository(uuidOfRepository, path)

data class RepositoryWithNoAuthorization(
        private val uuidOfRepository: String,
        private val path: String
) : Repository(uuidOfRepository, path)