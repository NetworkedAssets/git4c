package com.networkedassets.git4c.core.bussiness

import org.apache.commons.io.FilenameUtils
import java.nio.file.Path
import java.util.*

data class ImportedFileData(
        val path: String,
        val context: Path,
        private val updateAuthorFullNameFun: () -> String,
        private val updateAuthorEmailFun: () -> String,
        private val updateDateFun: () -> Date,
        private val contentFun: () -> ByteArray
) {
    fun content() = contentFun()

    val updateDate by lazy { updateDateFun() }

    val updateAuthorFullName by lazy { updateAuthorFullNameFun() }

    val updateAuthorEmail by lazy { updateAuthorEmailFun() }

    fun getAbsolutePath(): Path = context.resolve(path)

    val extension: String get() = FilenameUtils.getExtension(path)
}
