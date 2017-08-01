package com.networkedassets.git4c.infrastructure.plugin.converter.markdown

import com.networkedassets.git4c.utils.Patterns
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.core.exceptions.ConDocException
import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem
import com.networkedassets.git4c.infrastructure.UuidIdentifierGenerator
import com.networkedassets.git4c.infrastructure.plugin.converter.markdown.tables.TablesExtension
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceFileReader
import org.apache.commons.lang3.StringEscapeUtils
import org.apache.commons.lang3.StringUtils
import org.commonmark.Extension
import org.commonmark.node.Code
import org.commonmark.node.Node
import org.commonmark.node.Text
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.AttributeProvider
import org.commonmark.renderer.html.HtmlRenderer
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.parser.Parser.xmlParser
import org.jsoup.parser.Tag
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLConnection
import java.net.URLEncoder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.regex.Pattern

class MarkdownConverterPlugin : ConverterPlugin {

    private val log = LoggerFactory.getLogger(MarkdownConverterPlugin::class.java)

    private val extensions: List<Extension> = listOf(TablesExtension.create())
    private val parser: Parser
    //So we can manipulate randomness in testing
    private var generator: IdentifierGenerator

    init {
        parser = Parser.builder().extensions(extensions).build()
        generator = UuidIdentifierGenerator()
    }

    override fun convert(fileData: ImportedFileData): DocumentsItem? {
        if (fileData.getExtension().toLowerCase() == "md") {
            val content = fileData.getOriginalContent()
            val (tree, processedContent) = generateMarkdown(fileData.getAbsolutePath().toFile(), content, fileData.context)
            return DocumentsItem(fileData.path, fileData.updateAuthorFullName, fileData.updateAuthorEmail, fileData.updateDate, """<span>$processedContent</span>""", tree.toTableOfContents())
        } else {
            return null
        }
    }

    override val identifier: String = "markdown-converter"

    private fun generateMarkdown(file: File, originalContent: String, startDirectoryPath: Path): Pair<IndexTree, String> {
        val tree = IndexTree()
        val htmlRenderer = buildRenderer(file.toPath(), startDirectoryPath, tree)
        val node = parser.parse(originalContent)
        val baseMarkdown = parseLinks(htmlRenderer.render(node))
        return Pair(tree, baseMarkdown)
    }

    fun parseLinks(html: String): String {

        val document = Jsoup.parse(html, "", xmlParser())

        val elements = document.select(":matchesOwn(http(s?)://)")

        for (element in elements) {

            if (element.tag().name.toLowerCase() != "a") {

                for (node in element.textNodes()) {

                    val text = node.wholeText

                    val split = text.regexSplit(Patterns.WEB_URL)

                    val nodes = ArrayList<org.jsoup.nodes.Node>()

                    for (i in split.indices) {
                        if (i % 2 == 0) {
                            //Not matched text
                            nodes.add(TextNode(split[i], ""))
                        } else {
                            //Matched text
                            val newElement = Element(Tag.valueOf("a"), "")
                            newElement.attr("href", split[i])
                            newElement.appendChild(TextNode(split[i], ""))
                            nodes.add(newElement)
                        }
                    }

                    val sId = node.siblingIndex()
                    element.insertChildren(sId, nodes)
                    node.remove()
                }
            }
        }

        return document.html()
    }


    /**
     * Split text to array: [text before first match, first match, text between first and second match, second match, ...]
     */
    private fun String.regexSplit(pattern: Pattern): List<String> {
        var text = this

        val list = ArrayList<String>()
        val matcher = pattern.matcher(text)

        while (matcher.find()) {
            val g = matcher.group()
            val sub = StringUtils.substringBefore(text, g)
            list.add(sub)
            text = StringUtils.removeStart(text, sub)
            list.add(g)
            text = StringUtils.removeStart(text, g)
        }

        list.add(text)

        return list
    }

    private fun buildRenderer(path: Path, startDirectoryPath: Path, tree: IndexTree): HtmlRenderer {
        val treeNode = arrayOf(tree.masterNode)
        val ids = HashSet<String>()

        return HtmlRenderer.builder().extensions(extensions)
                .attributeProviderFactory {
                    AttributeProvider { node, tagName, attributes ->
                        if (tagName == "table") {
                            attributes.put("class", "aui git4c-table")
                        } else if (tagName == "code") {
                            val clz = attributes["class"] ?: ""
                            attributes.put("class", "$clz git4c-code")
                        } else if (tagName == "img") {
                            attributes.put("style", "max-width: 100%")
                            if (isLocalUri(attributes["src"])) {
                                val imageUrl = attributes["src"]!!

                                val imagePath: Path
                                val imageType: String

                                if (imageUrl.endsWith("puml")) {
                                    val f = Paths.get(path.parent.toUri().resolve(imageUrl))
                                    try {
                                        val reader = SourceFileReader(f.toFile(), Files.createTempDirectory("temp").toFile(), FileFormatOption(FileFormat.SVG))
                                        val images = reader.generatedImages
                                        imagePath = images[0].pngFile.toPath()
                                        imageType = "image/svg+xml"
                                    } catch (e: IOException) {
                                        log.error("Couldn't read puml $f", e)
                                        return@AttributeProvider
                                    } catch (e: IllegalArgumentException) {
                                        log.error("Couldn't read puml $f", e)
                                        return@AttributeProvider
                                    }
                                } else {
                                    imagePath = Paths.get(path.parent.toUri().resolve(imageUrl))
                                    imageType = URLConnection.guessContentTypeFromName(imagePath.fileName.toString())
                                }

                                try {
                                    val base64 = String(Base64.getEncoder().encode(Files.readAllBytes(imagePath)))
                                    val src = "data:$imageType;base64,$base64"
                                    attributes.put("src", src)
                                } catch (e: IOException) {
                                    log.error("Couldn't read image $imagePath", e)
                                    // image file not found, leave it as is
                                }

                            }
                        } else if (tagName == "a" && attributes.containsKey("href") && isLocalUri(attributes["href"])) {

                            val href = attributes["href"]!!
                            if (href.startsWith("#")) {
                                attributes.put("href", "javascript:void(0)")
                                attributes.put("v-on:click", "anchor('" + StringUtils.removeStart(href, "#") + "')")
                            } else {
                                val hrefWithoutHash = href.removePrefix("#")
                                val target = path.parent.toUri().resolve(hrefWithoutHash)
                                try {
                                    attributes.put("href", "#/" + URLEncoder.encode(startDirectoryPath.toUri().relativize(target).toString(), "UTF-8"))
                                } catch (e: UnsupportedEncodingException) {
                                    throw ConDocException(e)
                                }

                            }
                        } else if (tagName.matches(Regex("h\\d"))) {
                            val type = Integer.valueOf(tagName.substring(1))!!

                            val title = nodeToText(node) ?: return@AttributeProvider

                            var id = generator.generateNewIdentifier()

                            while (ids.contains(id)) {
                                id = generator.generateNewIdentifier()
                            }

                            ids.add(id)
                            attributes.put("name", id)

                            while (treeNode[0].id >= type) {
                                treeNode[0] = treeNode[0].parent!!
                            }

                            val newNode = IndexNode(type, ArrayList<IndexNode>(), title, id, treeNode[0])
                            treeNode[0].children.add(newNode)
                            treeNode[0] = newNode
                        }
                    }
                }
                .build()
    }

    fun Node.getLiteral() = when(this) {
        is Text -> literal
        is Code -> literal
        else -> null
    }

    fun nodeToText(node: Node): String? {

        val buffer = StringBuilder()

        val firstText = node.firstChild.getLiteral()

        if (firstText == null) {
            log.warn("Couldn't get text from first child: {}", node.firstChild)
            return null
        }

        buffer.append(firstText)

        var next = node.firstChild.next

        while (next != null) {

            val text = next.getLiteral()
            if (text == null) {
                log.warn("Couldn't get text from {}", next)
                return null
            }

            buffer.append(text)
            next = next.next
        }

        return buffer.toString().escapeHtml()
    }

    private fun String.escapeHtml(): String = StringEscapeUtils.escapeHtml4(this)

    private fun isLocalUri(uri: String?): Boolean {
        if (uri == null) {
            return false
        }
        return !uri.matches("^https?://.*".toRegex())
    }

}
