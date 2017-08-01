package com.networkedassets.git4c.core.bussiness

import org.apache.commons.io.FilenameUtils
import java.nio.file.Path
import java.util.*

data class ImportedFileData (
        val path: String,
        val context: Path,
        val updateAuthorFullName: String,
        val updateAuthorEmail: String,
        val updateDate: Date,
        val content: String
)
{
    fun getOriginalContent(): String {
        return content
    }

    fun getAbsolutePath(): Path {
        return context.resolve(path)
//        return path.split("/").dropLast(1).toString()
    }

    fun getExtension(): String {
        return FilenameUtils.getExtension(path)
    }

}
