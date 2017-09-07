package com.networkedassets.git4c.infrastructure.plugin.parser

import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.core.bussiness.Method
import com.networkedassets.git4c.core.bussiness.ParserPlugin
import com.networkedassets.git4c.infrastructure.plugin.parser.gherkin.GherkinParser
import com.networkedassets.git4c.infrastructure.plugin.parser.java.JavaParser
import org.slf4j.LoggerFactory

class Parsers : ParserPlugin {

    val logger = LoggerFactory.getLogger(this::class.java)

    override fun getMethods(file: ImportedFileData): List<Method> {
        if (file.extension == "feature") {
            return GherkinParser().getMethods(file.contentString)
        } else if (file.extension == "java") {
            return JavaParser().getMethods(file.contentString)
        } else {
            return listOf()
        }
    }

    override fun getMethod(file: ImportedFileData, methodName: String): Pair<ImportedFileData, Method?> {

        val methods = getMethods(file)
        val method = methods.find { it.name == methodName }

        if (method == null) {
            logger.warn("Couldn't find method {} in file {}", method, file.path)
            return Pair(file, null)
        }

        val range = method.range

        val methodLines = file.contentString.lines().slice(range.start..range.end)
        val str = methodLines.joinToString(separator = "\n")
        val f = file.copy(contentFun = { str.toByteArray() })
        return Pair(f, method)
    }
}