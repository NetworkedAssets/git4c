package com.networkedassets.git4c.infrastructure.plugin.converter.markdown

import com.networkedassets.git4c.data.macro.documents.item.TableOfContents

data class IndexNode(
        val id: Int,
        val children: MutableList<IndexNode> = mutableListOf(),
        val name: String = "",
        val anchorName: String = "",
        val parent: IndexNode?
) {
    fun toTableOfContents(): TableOfContents =
            TableOfContents(name, anchorName, children.map { it.toTableOfContents() })

    override fun toString(): String {
        return "IndexNode(id=$id, name='$name')"
    }
}
