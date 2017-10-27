package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.core.business.ExtractionResult
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.core.bussiness.ParserPlugin
import com.networkedassets.git4c.core.bussiness.Range
import com.networkedassets.git4c.core.datastore.extractors.EmptyExtractor
import com.networkedassets.git4c.core.datastore.extractors.ExtractorData
import com.networkedassets.git4c.core.datastore.extractors.LineNumbersExtractorData
import com.networkedassets.git4c.core.datastore.extractors.MethodExtractorData

class ExtractContentProcess(
        val parser: ParserPlugin
) {

    fun extract(extractorData: ExtractorData?, file: ImportedFileData): ExtractionResult {

        if (extractorData == null) {
            return wholeFile(file)
        }

        return when(extractorData) {
            is EmptyExtractor -> wholeFile(file)
            is LineNumbersExtractorData -> {
                val range = Range(extractorData.startLine, extractorData.endLine)
                getLines(file, range)
            }
            is MethodExtractorData -> {
                val method = parser.getMethod(file, extractorData.method)
                if (method == null) {
                    wholeFile(file)
                } else {
                    getLines(file, method.range)
                }

            }
            else -> throw RuntimeException("Unknown extractorData: ${extractorData::class.java}")
        }

    }

    private fun wholeFile(file: ImportedFileData) = ExtractionResult(file.contentString, 1)

    private fun getLines(file: ImportedFileData, range: Range): ExtractionResult {
        val methodLines = file.contentString.lines().slice(range.start-1 until range.end)
        val str = methodLines.joinToString(separator = "\n")
        return ExtractionResult(str, range.start)
    }

}