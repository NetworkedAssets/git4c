package com.networkedassets.git4c.infrastructure.plugin.source.git

import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.core.exceptions.ConDocException
import org.apache.commons.io.FilenameUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.util.*

class ImportedSourceFile(private val git: Git, repository: File, private val file: File) {

    private val path: Path = repository.toPath().relativize(file.toPath())

    fun getOriginalContent(): ByteArray {
        try {
            return file.readBytes()
        } catch (e: IOException) {
            throw ConDocException(e)
        }
    }

    fun getSourcePath(): Path {
        return git.repository.directory.toPath()
    }

    fun getPath(): Path {
        return path
    }

    fun lastCommitFunction(): RevCommit {
        try {
            val log = git.log().addPath(FilenameUtils.separatorsToUnix(path.toString())).call()
            return log.filterNotNull().iterator().next()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    val lastComit by lazy { lastCommitFunction() }


    fun isInternalGitFile(): Boolean {
        return path.toString().startsWith(".git")
    }

    fun convert(): ImportedFileData {
        return ImportedFileData(
                path = FilenameUtils.separatorsToUnix(path.toString()),
                context = getSourcePath().parent,
                updateAuthorFullNameFun = { lastComit.authorIdent.name },
                updateAuthorEmailFun = { lastComit.authorIdent.emailAddress },
                updateDateFun = { Date(lastComit.commitTime.toLong() * 1000) },
                contentFun = { getOriginalContent() }
        )
    }
}
