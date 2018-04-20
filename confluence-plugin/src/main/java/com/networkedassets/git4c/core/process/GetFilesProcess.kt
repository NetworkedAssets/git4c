package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.boundary.outbound.FilesList
import com.networkedassets.git4c.core.business.FileIgnorer
import com.networkedassets.git4c.core.bussiness.DocumentsTreeConverter
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.macro.documents.item.DocumentsFileIndex

class GetFilesProcess(
        val importer: SourcePlugin,
        val fileIgnorer: FileIgnorer
) {

    fun getFiles(repository: Repository, branch: String): FilesList {
        return importer.pull(repository, branch).use { files ->

            val filesToIgnore = files.imported.flatMap { fileIgnorer.getFilesToIgnore(it) }.toSet()

            val allFileNames = files.imported.map { it.path }.sorted()

            val fileNames = allFileNames.subtract(filesToIgnore).toList()

            val items = fileNames.map { createDocumentsItem(it) }
            val tree = DocumentsTreeConverter.treeify(items)
            FilesList(fileNames, tree)
        }
    }


    private fun createDocumentsItem(path: String): DocumentsFileIndex {
        return DocumentsFileIndex(path)
    }

}
