package com.networkedassets.git4c.data.macro.documents.item

import com.networkedassets.git4c.core.datastore.extractors.ExtractorData
import java.util.*

abstract class DocumentsFile(val path: String) {
    val index = path

    val name: String
        get() = path.split("/").last()

    val locationPath: List<String>
        get() = path.split("/").toMutableList().dropLast(1)

}

data class DocumentsFileIndex(private val pathToData: String) : DocumentsFile(pathToData)

data class ConvertedDocumentsItem(
        private val pathToFile: String,
        val updateAuthorFullName: String,
        val updateAuthorEmail: String,
        val updateDate: Date,
        val rawContent: String,
        val content: String,
        val tableOfContents: TableOfContents
) : DocumentsFile(pathToFile)

data class DocumentsItem(
        private val pathToFile: String,
        val updateAuthorFullName: String,
        val updateAuthorEmail: String,
        val updateDate: Date,
        val rawContent: String,
        val content: String,
        val tableOfContents: TableOfContents,
        val repositoryPath: String,
        val repositoryBranch: String,
        val extractorData: ExtractorData?
) : DocumentsFile(pathToFile) {

    constructor(repositoryPath: String, repositoryBranch: String, convertedDocumentsItem: ConvertedDocumentsItem, extractorData: ExtractorData?) : this(
            convertedDocumentsItem.path,
            convertedDocumentsItem.updateAuthorFullName,
            convertedDocumentsItem.updateAuthorEmail,
            convertedDocumentsItem.updateDate,
            convertedDocumentsItem.rawContent,
            convertedDocumentsItem.content,
            convertedDocumentsItem.tableOfContents,
            repositoryPath,
            repositoryBranch,
            extractorData
    )

    val uuid = repositoryPath + "_" + repositoryBranch + "_" + pathToFile + "_" + (extractorData?.uuid ?: "0")

}