package com.networkedassets.git4c.data.tableofcontent

class IndexTree(
        val masterNode: IndexNode = IndexNode(-1, parent = null)
) {
    fun toTableOfContents() = masterNode.toTableOfContents()
}