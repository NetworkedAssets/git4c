package com.networkedassets.git4c.core.business

import com.networkedassets.git4c.boundary.outbound.DocumentationsContentTree
import com.networkedassets.git4c.core.bussiness.DocumentsTreeConverter
import com.networkedassets.git4c.data.macro.documents.item.DocumentsFileIndex
import spock.lang.Specification

class DocumentsTreeConverterTest extends Specification {

    def createDocumentsItem(String path) {
        return new DocumentsFileIndex(path)
    }

    def static DIR = DocumentationsContentTree.NodeType.DIR
    def static ITEM = DocumentationsContentTree.NodeType.DOCITEM

    def "should create tree from doc items"() {
        given: "4 doc items in different folders"
        /*
          root
         └── doc
             ├── dir1
             │   ├── file11.md
             │   └── file12.md
             ├── dir2
             │   └── dir21
             │       └── file211.md
             └── file1.md
        */
        def documentsItems = [
                createDocumentsItem("doc/file1.md"),
                createDocumentsItem("doc/dir1/file11.md"),
                createDocumentsItem("doc/dir1/file12.md"),
                createDocumentsItem("doc/dir2/dir21/file211.md"),
        ]

        when: "doc items are treeified"
        DocumentationsContentTree root = DocumentsTreeConverter.treeify(documentsItems)

        then: "the tree is correct"
        root.fullName == ""
        root.name == ""
        root.type == DIR
        root.children.size() == 1
        DocumentationsContentTree doc = root.children[0]

        and: "doc"
        doc.fullName == "doc"
        doc.name == "doc"
        doc.type == DIR
        doc.children.size() == 3
        DocumentationsContentTree dir1 = doc.children[0]
        DocumentationsContentTree dir2 = doc.children[1]
        DocumentationsContentTree file1 = doc.children[2]

        and: "file1.md"
        file1.fullName == "doc/file1.md"
        file1.name == "file1.md"
        file1.type == ITEM
        file1.children.size() == 0

        and: "dir1"
        dir1.fullName == "doc/dir1"
        dir1.name == "dir1"
        dir1.type == DIR
        dir1.children.size() == 2
        DocumentationsContentTree file11 = dir1.children[0]
        DocumentationsContentTree file12 = dir1.children[1]

        and: "file11"
        file11.fullName == "doc/dir1/file11.md"
        file11.name == "file11.md"
        file11.type == ITEM
        file11.children.size() == 0

        and: "file12"
        file12.fullName == "doc/dir1/file12.md"
        file12.name == "file12.md"
        file12.type == ITEM
        file12.children.size() == 0

        and: "dir2"
        dir2.fullName == "doc/dir2/dir21"
        dir2.name == "dir2/dir21"
        dir2.type == DIR
        dir2.children.size() == 1
        DocumentationsContentTree file211 = dir2.children[0]

        and: "file211"
        file211.fullName == "doc/dir2/dir21/file211.md"
        file211.name == "file211.md"
        file211.type == ITEM
        file211.children.size() == 0

    }

}