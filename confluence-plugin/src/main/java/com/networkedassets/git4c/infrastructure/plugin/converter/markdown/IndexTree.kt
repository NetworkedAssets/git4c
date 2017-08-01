package com.networkedassets.git4c.infrastructure.plugin.converter.markdown

class IndexTree(
        val masterNode: IndexNode = IndexNode(-1, parent = null)
) {
    fun toTableOfContents() = masterNode.toTableOfContents()
}