package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.core.business.FileIgnorer
import com.networkedassets.git4c.core.business.FileIgnorerList
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.infrastructure.mocks.core.DirectorySourcePlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.main.asciidoc.AsciidocConverterPlugin
import com.networkedassets.git4c.utils.genTransactionId
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import kotlin.test.assertNotNull

class GetFilesProcessTest() {

    val importer = DirectorySourcePlugin()
    val fileIgnorer = FileIgnorerList(AsciidocConverterPlugin.get(false))

    val process = GetFilesProcess(importer, fileIgnorer)

    @Test
    fun `Get Files should return files tree from repository`() {

        // Given
        val branch = "master";
        val file = "README.md"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), "src/test/resources", false)

        // When
        val answer = process.getFiles(repository, branch);

        // Then
        assertNotNull(answer.tree)
        assertNotNull(answer.files)

        assertThat(answer.files.contains(file))

        assert(answer.tree.getChildByName(file).isPresent)
        assertThat(answer.tree.getChildByName(file).get().fullName).isEqualTo(file)
    }

    @Test
    fun `Get files shouldn't return ignored files`() {

        val ignorer = object: FileIgnorer {
            override fun getFilesToIgnore(fileData: ImportedFileData): List<String> {
                return listOf("file2.txt")
            }

            override fun supportedExtensions() = listOf("txt")
        }

        val process = GetFilesProcess(importer, ignorer)

        val repository = RepositoryWithNoAuthorization(genTransactionId(), "src/test/resources/core/process/getfilesprocess/ignoretest", false)

        val answer = process.getFiles(repository, "master")

        val tree = answer.tree

        assertThat(tree.getChildren()).hasSize(1)
        assertThat(tree.getChildren().first().name).isEqualTo("file1.txt")

    }

}