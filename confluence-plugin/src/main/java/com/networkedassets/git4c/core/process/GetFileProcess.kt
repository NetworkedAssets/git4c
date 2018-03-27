package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.boundary.outbound.FileContent
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.macro.documents.item.TableOfContents.Companion.EMPTY
import java.util.*

class GetFileProcess(
        val importer: SourcePlugin,
        val converter: ConverterPlugin,
        private val extractContentProcess: ExtractContentProcess
) {

    @Throws(NoSuchElementException::class)
    fun getFile(repository: Repository, branch: String, requestedFile: String): FileContent {
        return importer.get(repository, branch).use { files ->
            val file = files.imported.first() { it.path == requestedFile }
            val result = extractContentProcess.extract(null, file)
            val item = converter.convert(file, result)
            FileContent(item?.content ?: "", item?.tableOfContents ?: EMPTY)
        }
    }

}
