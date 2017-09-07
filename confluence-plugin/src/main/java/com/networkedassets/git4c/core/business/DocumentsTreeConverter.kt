package com.networkedassets.git4c.core.bussiness

import com.networkedassets.git4c.boundary.outbound.DocumentationsContentTree
import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem
import org.apache.commons.io.FilenameUtils


object DocumentsTreeConverter {

    @JvmStatic
    fun treeify(docItems: Collection<DocumentsItem>): DocumentationsContentTree {
        val root = DocumentationsContentTree("", "", DocumentationsContentTree.NodeType.DIR)

        for (docItem in docItems) {
            var last = root
            for (name in docItem.locationPath) {
                last = last.getOrCreateDirChild(name)
            }
            last.getOrCreateItemChild(FilenameUtils.getName(docItem.path))
        }

        root.normalize()

        return root
    }

}
