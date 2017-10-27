package com.networkedassets.git4c.data


data class CommitInfo(
    val id: String,
    val authorName: String,
    val message: String,
    val timeInMs: Long
)