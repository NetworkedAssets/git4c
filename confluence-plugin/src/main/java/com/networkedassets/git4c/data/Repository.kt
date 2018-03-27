package com.networkedassets.git4c.data


abstract class Repository(
        val uuid: String,
        val repositoryPath: String,
        val isEditable: Boolean
)

data class RepositoryWithUsernameAndPassword(
        private val uuidOfRepository: String,
        private val path: String,
        private val isEditable_: Boolean,
        val username: String,
        val password: String
) : Repository(uuidOfRepository, path, isEditable_)

data class RepositoryWithSshKey(
        private val uuidOfRepository: String,
        private val path: String,
        private val isEditable_: Boolean,
        val sshKey: String
) : Repository(uuidOfRepository, path, isEditable_)

data class RepositoryWithNoAuthorization(
        private val uuidOfRepository: String,
        private val path: String,
        private val isEditable_: Boolean
) : Repository(uuidOfRepository, path, isEditable_)