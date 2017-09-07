package com.networkedassets.git4c.data.macro.documents.item

import java.util.*

data class DocumentsItem(
        val path: String,
        val updateAuthorFullName: String,
        val updateAuthorEmail: String,
        val updateDate: Date,
        val rawContent: String,
        val content: String,
        val tableOfContents: TableOfContents
) {

    val index = path

    val name: String
        get() = path.split("/").last()

    val locationPath: List<String>
        get() = path.split("/").toMutableList().dropLast(1)

}