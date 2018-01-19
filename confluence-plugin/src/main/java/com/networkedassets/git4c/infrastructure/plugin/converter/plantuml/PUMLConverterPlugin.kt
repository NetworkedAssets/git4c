package com.networkedassets.git4c.infrastructure.plugin.converter.plantuml

import com.networkedassets.git4c.core.business.ExtractionResult
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.data.macro.documents.item.ConvertedDocumentsItem
import com.networkedassets.git4c.data.macro.documents.item.TableOfContents
import com.networkedassets.git4c.infrastructure.plugin.converter.main.markdown.InternalConverterPlugin
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceFileReader
import java.nio.file.Files
import java.util.*

class PUMLConverterPlugin : InternalConverterPlugin {

    override fun convert(fileData: ImportedFileData, extractionResult: ExtractionResult): ConvertedDocumentsItem? {
        val content = fileData.content()

        val tempFile = Files.createTempFile(null, null).toFile()

        tempFile.writeText(String(content))

        val reader = SourceFileReader(tempFile, Files.createTempDirectory("temp").toFile(), FileFormatOption(FileFormat.SVG))
        val images = reader.generatedImages
        val imagePath = images[0].pngFile

        val b64PumlImage = String(Base64.getEncoder().encode(imagePath.readBytes()))

        val pageContent = """
            <div>
                <img src="data:image/svg+xml;base64,$b64PumlImage" class="git4c-image">
            </div>
        """

        return ConvertedDocumentsItem(fileData.path, fileData.updateAuthorFullName, fileData.updateAuthorEmail, fileData.updateDate, String(content), pageContent, TableOfContents("", "", listOf()))
    }

    override fun supportedExtensions() = listOf("puml")


    override val identifier = "PUMLConverter"
}