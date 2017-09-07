package com.networkedassets.git4c.infrastructure.plugin.parser.gherkin

import com.networkedassets.git4c.core.bussiness.Method
import com.networkedassets.git4c.core.bussiness.Parser
import com.networkedassets.git4c.core.bussiness.Range

class GherkinParser : Parser {

    override fun getMethods(content: String): List<Method> {

        val lines = content.lines()

        val ranges = mutableListOf<ArrayList<Int>>()
        var currentList = arrayListOf<Int>()

        lines
                .forEachIndexed { i, line ->
                    val l = line.trim()
                    if (l.startsWith("Scenario:")) {
                        ranges.add(currentList)
                        currentList = arrayListOf()
                    }
                    currentList.add(i)
                }

        ranges.add(currentList)

        return ranges.map {
            val r = it.filter { lines[it].isNotEmpty() }
            val name = lines[r.first()].trim().removePrefix("Scenario:").trim()
            Method(name, Range(r.first(), r.last()))
        }.drop(1)
    }
}