package com.networkedassets.git4c.boundary.outbound

import com.networkedassets.git4c.data.macro.documents.item.TableOfContents

data class FileContent(
        val content: String,
        val tableOfContents: TableOfContents
)