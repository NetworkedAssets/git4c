package com.networkedassets.git4c.boundary.outbound

data class FilesList(
        val files: List<String>,
        val tree: DocumentationsContentTree
)