package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.core.bussiness.Method
import com.networkedassets.git4c.core.bussiness.ParserPlugin
import com.networkedassets.git4c.core.bussiness.Range
import com.networkedassets.git4c.core.datastore.extractors.LineNumbersExtractorData
import com.networkedassets.git4c.core.datastore.extractors.MethodExtractorData
import com.networkedassets.git4c.utils.genTransactionId
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito
import java.nio.file.Paths
import java.util.*

class ExctractContentProcessTest {

    @Test
    fun `Extracting of lines range from source file should return only content between those lines`() {

        // Given
        val process = ExtractContentProcess(Mockito.mock(ParserPlugin::class.java))
        val resourceDirectory = Paths.get("src/test/resources", "sourcefiles/gherkin/file2.feature");
        val startLine = 5
        val endLine = 11
        val fileContent = String(resourceDirectory.toFile().readBytes())
        val file = ImportedFileData("", resourceDirectory, { "" }, { "" }, { Date() }, {
            fileContent.toByteArray()
        })

        // When
        val result = process.extract(LineNumbersExtractorData(genTransactionId(), startLine, endLine), file)

        // Then
        assertThat(result.content).startsWith(fileContent.lines().get(startLine - 1))
        assertThat(result.content).endsWith(fileContent.lines().get(endLine - 1));
        assertThat(result.content.lines()).hasSize(endLine - startLine + 1)
    }

    @Test
    fun `Extracting of method in file should return the method with it's body`() {

        // Setup
        val parserPlugin = Mockito.mock(ParserPlugin::class.java)
        val process = ExtractContentProcess(parserPlugin)

        // Given
        val resourceDirectory = Paths.get("src/test/resources", "sourcefiles/gherkin/file2.feature");
        val fileContent = String(resourceDirectory.toFile().readBytes())
        val file = ImportedFileData("", resourceDirectory, { "" }, { "" }, { Date() }, {
            fileContent.toByteArray()
        })

        val methodName = "Background"
        val startLine = 3
        val endLine = 7


        Mockito.`when`(parserPlugin.getMethod(file, methodName)).thenAnswer { Method(methodName, Range(3, 7)) }

        // When
        val result = process.extract(MethodExtractorData(genTransactionId(), "Background"), file)

        // Then
        assertThat(result.content).startsWith(fileContent.lines().get(startLine - 1))
        assertThat(result.content).endsWith(fileContent.lines().get(endLine - 1));
        assertThat(result.content.lines()).hasSize(endLine - startLine + 1)
    }

    @Test
    fun `Extracting of method in file should return complete body of file if method has not been found`() {

        // Setup
        val parserPlugin = Mockito.mock(ParserPlugin::class.java)
        val process = ExtractContentProcess(parserPlugin)

        // Given
        val resourceDirectory = Paths.get("src/test/resources", "sourcefiles/gherkin/file2.feature");
        val fileContent = String(resourceDirectory.toFile().readBytes())
        val file = ImportedFileData("", resourceDirectory, { "" }, { "" }, { Date() }, {
            fileContent.toByteArray()
        })

        val methodName = "NotExist"

        Mockito.`when`(parserPlugin.getMethod(file, methodName)).thenAnswer { null }

        // When
        val result = process.extract(MethodExtractorData(genTransactionId(), methodName), file)

        // Then
        assertThat(result.content).startsWith(fileContent.lines().first())
        assertThat(result.content).endsWith(fileContent.lines().last());
        assertThat(result.content.lines()).hasSize(fileContent.lines().size)
    }
}