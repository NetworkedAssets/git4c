package com.networkedassets.git4c.boundary.outbound

abstract class SimpleExtractorData(
        val type: String
)

data class SimpleMethod(
        val name: String
): SimpleExtractorData(type = "METHOD")

data class LineRange(
        val startLine: Int,
        val endLine: Int
): SimpleExtractorData(type = "LINES")