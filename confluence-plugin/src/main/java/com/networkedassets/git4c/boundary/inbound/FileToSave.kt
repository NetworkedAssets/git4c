package com.networkedassets.git4c.boundary.inbound

data class FileToSave(
        val file: String,
        val content: String,
        val commitMessage: String,
        var branch: String? = null
)