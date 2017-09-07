package com.networkedassets.git4c.boundary.outbound

data class Files(
        val files: List<String>,
        val tree: DocumentationsContentTree
)