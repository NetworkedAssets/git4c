package com.networkedassets.git4c.core.business

class DefaultGlobsMap() {
    val defaultGlobs = listOf(
            "Gherkin" to "feature",
            "Kotlin" to "kt",
            "Scala" to "scala",
            "Java" to "java",
            "Markdown" to "md"
    ).map {
        val ext = it.second
        Pair(it.first, "**.$ext")
    }.toMap()
}