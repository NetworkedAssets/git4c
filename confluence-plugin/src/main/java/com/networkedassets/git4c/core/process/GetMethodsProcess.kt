package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.boundary.outbound.Method
import com.networkedassets.git4c.boundary.outbound.Methods
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.ParserPlugin
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.extractors.LineNumbersExtractorData
import com.networkedassets.git4c.data.Repository

class GetMethodsProcess(
        val importer: SourcePlugin,
        val converter: ConverterPlugin,
        val parsers: ParserPlugin,
        val extractorContentProcess: ExtractContentProcess
) {

    fun getMethods(repository: Repository, branch: String, file: String): Methods {

        return importer.pull(repository, branch).use { importedFiles ->
            val files = importedFiles.imported

            val gitFile = files.first { it.path == file }

            val methods = parsers.getMethods(gitFile)

            Methods(methods.mapNotNull {
                val range = it.range
                val extractor = LineNumbersExtractorData("", range.start, range.end)
                val extractionResult = extractorContentProcess.extract(extractor, gitFile)
                val fileResult = converter.convert(gitFile, extractionResult)

                if (fileResult != null) {
                    Method(it.name, fileResult.content)
                } else {
                    null
                }
            })
        }

    }

}