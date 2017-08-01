package com.networkedassets.git4c.infrastructure.plugin.source.git

import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.core.exceptions.ConDocException
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit

import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.Path
import java.util.Date

class ImportedSourceFile(private val git: Git, repository: File, private val file: File) {

    private val path: Path = repository.toPath().relativize(file.toPath())

    fun getOriginalContent(): String {
        try {
            return FileUtils.readFileToString(file, Charset.defaultCharset())
        } catch (e: IOException) {
            throw ConDocException(e)
        }
    }

    fun getAbsolutePath(): Path {
        return file.toPath()
    }

    fun getSourcePath(): Path {
        return git.repository.directory.toPath()
    }

    fun getPath(): Path {
        return path
    }

    private val lastCommit: RevCommit
        get() {
            try {
                val log = git.log().addPath(FilenameUtils.separatorsToUnix(path.toString())).call()
                return log.iterator().next()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

    fun isInternalGitFile(): Boolean {
        return path.toString().startsWith(".git")
    }

    fun convert(): ImportedFileData {
        val commit = lastCommit
        val name = commit.authorIdent.name
        val email = commit.authorIdent.emailAddress
        val content = getOriginalContent()
        return ImportedFileData(
                path = FilenameUtils.separatorsToUnix(path.toString()),
                context = getSourcePath().parent,
                updateAuthorFullName = name,
                updateAuthorEmail = email,
                updateDate = Date(commit.commitTime.toLong() * 1000),
                content = content
        )
    }
}
