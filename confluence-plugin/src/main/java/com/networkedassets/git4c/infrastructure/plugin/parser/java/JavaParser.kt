package com.networkedassets.git4c.infrastructure.plugin.parser.java

import com.networkedassets.git4c.JavaLexer
import com.networkedassets.git4c.JavaParser
import com.networkedassets.git4c.core.bussiness.Method
import com.networkedassets.git4c.core.bussiness.Parser
import com.networkedassets.git4c.core.bussiness.Range
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class JavaParser: Parser {

    override fun getMethods(content: String): List<Method> {

        val lines = content.lines()

        val lexer = JavaLexer(CharStreams.fromString(content))
        val parser = JavaParser(CommonTokenStream(lexer))
        val ctx = parser.compilationUnit()

        val phase1Methods = ctx.typeDeclaration()
                .mapNotNull { it.classDeclaration()?.classBody()?.classBodyDeclaration() }
                .flatten()
                .filter { it?.memberDeclaration()?.methodDeclaration() != null }
                .map { Phase1Method(it!!.memberDeclaration()!!.methodDeclaration().IDENTIFIER().toString(), Range(it.start.line - 1, it.stop.line - 1)) }

        //Find javadocs
        val methods = phase1Methods.map {
            val start = it.range.start - 1
            if (lines[start].trim() == "*/") {
                //Javadoc is there

                //New version
                val diff = lines.take(start).asReversed().indexOfFirst { it.trim().startsWith("/*") } + 1

                //Old version
//                while (!lines[start].trim().startsWith("/*")) {
//                    start--
//                }

                Method(it.name, it.range.copy(start = start - diff))

            } else {
                Method(it.name, it.range)
            }

        }

        return methods
    }

    data class Phase1Method(val name: String, val range: Range)

}