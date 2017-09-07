package com.networkedassets.git4c.test

import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.infrastructure.plugin.source.directory.DirectorySourcePlugin

import java.nio.file.Path

class Utils {

    static Collection<ImportedFileData> getDataFromDirectory(Path path) {
        def settings = new MacroSettings("macro", "repository", "master", "", "")
        def repository = new RepositoryWithNoAuthorization("repository", path.toFile().absolutePath)
        return new DirectorySourcePlugin().pull(repository, settings.branch).imported
    }

}
