package com.networkedassets.git4c.test

import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.infrastructure.mocks.core.DirectorySourcePlugin

import java.nio.file.Path

class Utils {

    static Collection<ImportedFileData> getDataFromDirectory(Path path) {
        def settings = new MacroSettings("macro", "repository", "master", "", "", null)
        def repository = new RepositoryWithNoAuthorization("repository", path.toFile().absolutePath, false)
        return new DirectorySourcePlugin().pull(repository, settings.branch, false).imported
    }

}
