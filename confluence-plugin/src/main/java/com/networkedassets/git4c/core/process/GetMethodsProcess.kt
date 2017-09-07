package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.boundary.outbound.Method
import com.networkedassets.git4c.boundary.outbound.Methods
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.ParserPlugin
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.data.Repository

class GetMethodsProcess(
        val importer: SourcePlugin,
        val converter: ConverterPlugin,
        val parsers: ParserPlugin
) {

    fun getMethods(repository: Repository, branch: String, file: String): Methods {

        return importer.pull(repository, branch).use { importedFiles ->
            val files = importedFiles.imported

            val gitFile = files.first { it.path == file }
            val fileContent = gitFile.contentString

            val methods = parsers.getMethods(gitFile)

            Methods(methods.map {
                val range = it.range
                val content = fileContent.lines().slice(range.start..range.end)
                val str = content.joinToString(separator = "\n")
                val f = gitFile.copy(contentFun = { str.toByteArray() })

                Method(it.name, converter.convert(f, range.start)!!.content)
            })
        }

    }

}