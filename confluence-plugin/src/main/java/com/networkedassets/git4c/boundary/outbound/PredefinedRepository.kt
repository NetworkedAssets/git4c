package com.networkedassets.git4c.boundary.outbound

data class PredefinedRepository(
        val uuid: String,
        val sourceRepositoryUrl: String,
        val authType: String,
        val name: String,
        val editable: Boolean
)