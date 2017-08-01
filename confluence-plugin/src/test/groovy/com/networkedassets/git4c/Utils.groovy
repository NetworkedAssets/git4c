package com.networkedassets.git4c

import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.data.macro.DocumentationsMacroSettings
import com.networkedassets.git4c.data.macro.NoAuthCredentials
import com.networkedassets.git4c.infrastructure.plugin.source.directory.DirectorySourcePlugin

import java.nio.file.Path

class Utils {

    public static Collection<ImportedFileData> getDataFromDirectory(Path path) {
        def settings = new DocumentationsMacroSettings("", path.toFile().absolutePath, new NoAuthCredentials(), "", "", "")
        return new DirectorySourcePlugin().createFetchProcess(settings).fetch()
    }

}
