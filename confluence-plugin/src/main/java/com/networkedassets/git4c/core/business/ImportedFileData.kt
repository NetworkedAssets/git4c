package com.networkedassets.git4c.core.bussiness

import org.apache.commons.io.FilenameUtils
import java.nio.file.Path
import java.util.*

data class ImportedFileData(
        val path: String,
        val context: Path,
        val updateAuthorFullName: String,
        val updateAuthorEmail: String,
        val updateDate: Date,
        val contentFun: () -> ByteArray
) {

    val content by lazy { contentFun() }

    /**
     * Should only be used if we're sure that file is text
     */
    val contentString by lazy { String(content) }

    fun getAbsolutePath(): Path {
        return context.resolve(path)
//        return path.split("/").dropLast(1).toString()
    }

    val extension: String
    get() = FilenameUtils.getExtension(path)

//    fun getExtension(): String {
//        return FilenameUtils.getExtension(path)
//    }

}
