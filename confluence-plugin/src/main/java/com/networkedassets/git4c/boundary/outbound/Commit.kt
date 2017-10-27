package com.networkedassets.git4c.boundary.outbound

abstract class Commit

data class BasicCommitInfo(
        val id : String,
        val authorName: String,
        val message: String,
        val date: Long
): Commit()