package com.networkedassets.git4c.infrastructure.plugin.converter.main

import com.networkedassets.git4c.core.business.Macro
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.core.exceptions.ConDocException
import com.networkedassets.git4c.data.macro.documents.item.TableOfContents
import com.networkedassets.git4c.data.tableofcontent.IndexNode
import com.networkedassets.git4c.data.tableofcontent.IndexTree
import com.networkedassets.git4c.utils.Patterns
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceFileReader
import org.apache.commons.text.StringEscapeUtils
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.Jsoup.parse
import org.jsoup.nodes.Document
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

class JSoupPostProcessor(
        private val identifierGenerator: IdentifierGenerator
) : ConverterPostProcessor {

    private val log = LoggerFactory.getLogger(JSoupPostProcessor::class.java)

    override fun parse(result: String, file: File, startDirectory: Path, macro: Macro): String {
        val jsoup = parse(result, "", xmlParser())

        inlineImages(jsoup, file)
        addClassToCode(jsoup)
        addClassToTables(jsoup)
        parseAnchors(jsoup, file, startDirectory, macro)
        return parseLinks(jsoup.html())
    }

    override fun generateTableOfContents(result: String): Pair<String, TableOfContents> {

        val tree = IndexTree()
        val treeNode = arrayOf(tree.masterNode)
        val ids = HashSet<String>()

        val jsoup = parse(result, "", xmlParser())

        val hTags = jsoup.select("h1, h2, h3, h4, h5, h6");

        for (hTag in hTags) {

            val randomIdentifierSequence = generateSequence { identifierGenerator.generateNewIdentifier() }

            val tagName = hTag.tagName()

            val type = Integer.valueOf(tagName.substring(1))

            // Some parsers (like Asciidoctor) escapes html while others (like Commonmark) doesn't. This will make sure
            // that in both cases we'll get escaped html
            val title = StringEscapeUtils.escapeHtml4(StringEscapeUtils.unescapeHtml4(hTag.text()))

//            val title = hTag.text()

            val id = randomIdentifierSequence.filterNot { ids.contains(it) }.first()

            ids.add(id)
            hTag.attr("name", id)

            while (treeNode[0].id >= type) {
                treeNode[0] = treeNode[0].parent!!
            }

            val newNode = IndexNode(type, ArrayList(), title, id, treeNode[0])
            treeNode[0].children.add(newNode)
            treeNode[0] = newNode

        }

        return jsoup.html() to tree.toTableOfContents()
    }

    private fun inlineImages(html: Document, baseFile: File) {

        html.getElementsByTag("img")
                .asSequence()
                .filter { it.attr("src") != null }
                .map { it.addClass("git4c-image") }
                .filter { isLocalUri(it.attr("src")) }
                .forEach {
                    val url = it.attr("src")

                    val imageUrl = url

                    val imagePath: Path
                    val imageType: String?

                    val path = baseFile.toPath()

                    if (imageUrl.endsWith("puml")) {
                        val f = Paths.get(path.parent.toUri().resolve(imageUrl))
                        try {
                            val reader = SourceFileReader(f.toFile(), Files.createTempDirectory("temp").toFile(), FileFormatOption(FileFormat.SVG))
                            val images = reader.generatedImages
                            if (images.isEmpty()) {
                                it.remove()
                                return@forEach
                            }
                            imagePath = images.first().pngFile.toPath()
                            imageType = "image/svg+xml"
                        } catch (e: IOException) {
                            log.error("Couldn't read puml $f", e)
                            return@forEach
                        } catch (e: IllegalArgumentException) {
                            log.error("Couldn't read puml $f", e)
                            return@forEach
                        }
                    } else {
                        imagePath = Paths.get(path.parent.toUri().resolve(imageUrl))
                        imageType = getImageTypeForFile(imagePath.toFile().name)
                    }

                    if (imageType == null) {
                        it.tagName("a")
                        it.attr("href", imageUrl)
                        it.text(it.attr("alt"))
                        it.removeAttr("src")
                        it.removeAttr("alt")
                        it.removeAttr("class")
                    } else {

                        try {
                            val base64 = String(Base64.getEncoder().encode(Files.readAllBytes(imagePath)))
                            val src = "data:$imageType;base64,$base64"
                            it.attr("src", src)
                        } catch (e: Exception) {
                            log.warn("Couldn't read image $imagePath - ${e.javaClass.simpleName}")
                            // image file not found, leave it as is
                        }
                    }

                }
    }

    private fun getImageTypeForFile(fileName: String): String? {

        return if (fileName.endsWith(".svg")) {
            //URLConnection.guessContentTypeFromName doesn't like .svg files
            "image/svg+xml"
        } else {
            try {
                URLConnection.guessContentTypeFromName(fileName)
            } catch (e: IllegalStateException) {
                null
            }
        }

    }

    private fun addClassToTables(html: Document) {

        html.getElementsByTag("table")
                .forEach {
                    it.addClass("aui").addClass("git4c-table")
                }

    }

    private fun addClassToCode(html: Document) {

        html.getElementsByTag("code")
                .forEach {
                    it.addClass("git4c-code").addClass("git4c-highlightjs-code")
                }

    }

    private fun parseAnchors(html: Document, file: File, startDirectoryPath: Path, macro: Macro) {

        val path = file.toPath()

        html
                .getElementsByTag("a")
                .asSequence()
                .filter { it.attr("href") != null }
                .filter { isLocalUri(it.attr("href")) }
                .forEach {

                    val href = it.attr("href")
                    if (href.startsWith("#")) {
                        it.attr("href", "javascript:void(0)")
                        it.attr("v-on:click", "anchor('" + StringUtils.removeStart(href, "#") + "')")
                    } else {
                        val beforeHash = href.substringBefore("#")
                        val afterHash = href.substringAfter("#", "")

                        val target = path.parent.toUri().resolve(beforeHash)
                        if (File(target).exists() && macro.type != Macro.MacroType.SINGLEFILE) {
                            try {
                                //We can't just encode target - markdown requires "%20" instead of spaces which will
                                //encode to %2520 instead of %20. To fix this we replace %2520 with %20 after encoding
                                val encodedLocation = URLEncoder.encode(startDirectoryPath.toUri().relativize(target).toString()).replace("%2520", "%20")
                                it.attr("v-on:click", """moveToFile('$encodedLocation', '$afterHash')""")
                                it.attr("href", "javascript:void(0)")
                            } catch (e: UnsupportedEncodingException) {
                                throw ConDocException(e)
                            }
                        } else {
                            it.addClass("git4c-unclickable-link")
                            it.attr("href", "javascript:void(0)")
                        }
                    }


                }

    }

    private fun isLocalUri(uri: String): Boolean {
        return !uri.matches("^(https?://|mailto:).*".toRegex())
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

}