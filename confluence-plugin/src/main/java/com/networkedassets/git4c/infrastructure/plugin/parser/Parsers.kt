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
            return GherkinParser().getMethods(String(file.content()))
        } else if (file.extension == "java") {
            return JavaParser().getMethods(String(file.content()));
        } else {
            return listOf()
        }
    }

    override fun getMethod(file: ImportedFileData, methodName: String): Method? {

        val methods = getMethods(file)
        val method = methods.find { it.name == methodName }

        if (method == null) {
            logger.warn("Couldn't find method {} in file {}", method, file.path)
            return null
        }

        return method
    }
}