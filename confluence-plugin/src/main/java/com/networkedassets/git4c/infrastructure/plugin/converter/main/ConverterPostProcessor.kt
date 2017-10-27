package com.networkedassets.git4c.infrastructure.plugin.converter.main

import com.networkedassets.git4c.data.macro.documents.item.TableOfContents
import java.io.File
import java.nio.file.Path

interface ConverterPostProcessor {
    fun generateTableOfContents(result: String): Pair<String, TableOfContents>
    fun parse(result: String, file: File, startDirectory: Path): String
}