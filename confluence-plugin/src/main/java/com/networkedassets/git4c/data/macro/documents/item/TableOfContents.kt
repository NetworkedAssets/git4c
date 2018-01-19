package com.networkedassets.git4c.data.macro.documents.item

data class TableOfContents(
        val name: String,
        val anchorName: String,
        val children: List<TableOfContents>
) {
    companion object {
        val EMPTYTOC = TableOfContents("", "", listOf())
    }
}
