package com.networkedassets.git4c.boundary.outbound

import java.util.*

class DocumentationsContentTree(var name: String, var fullName: String, var type: DocumentationsContentTree.NodeType) {

    private var children: MutableList<DocumentationsContentTree> = ArrayList()


    fun getChildren(): List<DocumentationsContentTree> {
        return children
    }

    fun getChildByName(name: String): Optional<DocumentationsContentTree> {
        return children.stream().filter { c -> c.name == name }.findFirst()
    }

    fun getOrCreateDirChild(name: String): DocumentationsContentTree {
        return getOrCreateChild(name, NodeType.DIR)
    }

    fun getOrCreateItemChild(name: String): DocumentationsContentTree {
        return getOrCreateChild(name, NodeType.DOCITEM)
    }

    private fun getOrCreateChild(name: String, type: NodeType): DocumentationsContentTree {
        return getChildByName(name).orElseGet {
            val child = DocumentationsContentTree(name, generateFullName(name), type)
            this.children.add(child)
            sortChildren()
            child
        }
    }

    private fun generateFullName(name: String): String {
        return if (this.fullName == "")
            name
        else
            this.fullName + "/" + name
    }

    fun setName(name: String): DocumentationsContentTree {
        this.name = name
        return this
    }

    fun setFullName(fullName: String): DocumentationsContentTree {
        this.fullName = fullName
        return this
    }

    fun setChildren(children: MutableList<DocumentationsContentTree>): DocumentationsContentTree {
        this.children = children
        sortChildren()
        return this
    }

    private fun sortChildren() {
        this.children.sortWith(Comparator<DocumentationsContentTree> { o1, o2 -> o1.name.compareTo(o2.name, ignoreCase = true) })
    }

    fun setType(type: NodeType): DocumentationsContentTree {
        this.type = type
        return this
    }

    enum class NodeType {
        DOCITEM, DIR
    }
}
