package com.networkedassets.git4c.selenium


class UsernamePassword(
        url: String,
        val username: String,
        val password: String
) : RepoType("Http: Username + Password", url)

class NoAuth(
        url: String
) : RepoType("Http: No Authorization", url)


class SSHKey(
        url: String,
        val key: String
) : RepoType("SSH: Private Key", url)

abstract class RepoType(val spinnerName: String, val url: String)