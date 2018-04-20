package com.networkedassets.git4c.infrastructure.plugin.converter.images

import com.networkedassets.git4c.core.business.ExtractionResult
import com.networkedassets.git4c.core.business.Macro
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.data.macro.documents.item.ConvertedDocumentsItem
import com.networkedassets.git4c.data.macro.documents.item.TableOfContents
import com.networkedassets.git4c.infrastructure.plugin.converter.main.markdown.InternalConverterPlugin
import java.util.*

class ImageConverterPlugin : InternalConverterPlugin {

    override fun convert(fileData: ImportedFileData, extractionResult: ExtractionResult, macro: Macro): ConvertedDocumentsItem? {

        val extension = fileData.extension
        val b64Content = String(Base64.getEncoder().encode(fileData.getAbsolutePath().toFile().readBytes()))

        val imageType = when (extension) {
            "png" -> "png"
            "jpg" -> "jpg"
            "jpeg" -> "jpg"
            "svg" -> "svg+xml"
            else -> RuntimeException("Unsupported extension $extension")
        }

        val pageContent = """
            <div>
                <img src="data:image/$imageType;base64,$b64Content" class="git4c-image">
            </div>
        """

        return ConvertedDocumentsItem(fileData.path, fileData.updateAuthorFullName, fileData.updateAuthorEmail, fileData.updateDate, String(fileData.content()), pageContent, TableOfContents("", "", listOf()))

    }

    override fun supportedExtensions() = listOf("png", "jpg", "svg", "jpeg")

    override val identifier = "images"
}