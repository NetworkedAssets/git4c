package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.boundary.outbound.File
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.core.bussiness.SourcePlugin



class GetFileProcess (
        val importer: SourcePlugin,
        val converter: ConverterPlugin
)   {

    fun getFile(repository: Repository, branch: String, requestedFile: String): File {
        return importer.pull(repository, branch).use { files ->
            val file = files.imported.first { it.path == requestedFile }
            val item = converter.convert(file)
            File(item?.content ?: "")
        }
    }

}
