package com.networkedassets.git4c.markdown

import com.networkedassets.git4c.infrastructure.plugin.converter.markdown.MarkdownConverterPlugin
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

import static com.networkedassets.git4c.Utils.getDataFromDirectory

class ImageTest extends Specification {

    def "Http(s) links shouldn't be altered"() {

        given:
        def resourceDirectory = Paths.get("src/test/resources", "imageTest/httpImage/Markdown.md");
        def source = getDataFromDirectory(resourceDirectory.parent)
        def converter = new MarkdownConverterPlugin()

        when:
        def data = source.collect { converter.convert(it) }.grep()
        def webPage = data[0].content
        def xml = new XmlSlurper().parseText(webPage)
        def img = xml.p.img

        then:
        data.size() == 1
        img.@src == "https://octodex.github.com/images/yaktocat.png"
    }

    def "Images in subfolders should be converted to Base64"() {

        given:
        def resourceDirectory = Paths.get("src/test/resources", "imageTest/relativeImage/Markdown.md");
        def converter = new MarkdownConverterPlugin()
        def base64 = Files.readAllBytes(resourceDirectory.parent.resolve("img/NA_logo.png")).encodeBase64()
        def source = getDataFromDirectory(resourceDirectory.parent)

        when:
        def data = source.collect { converter.convert(it) }.grep()
        def webPage = data[0].content
        def xml = new XmlSlurper().parseText(webPage)
        def img = xml.p.img

        then:
        data.size() == 1
        img.@src == "data:image/png;base64,$base64"
    }

    def "Images in parent folders should also work"() {

        given:
        def resourceDirectory = Paths.get("src/test/resources", "imageTest/parentImage/markdown/Markdown.md");
        def converter = new MarkdownConverterPlugin()
        def source = getDataFromDirectory(resourceDirectory.parent)
        def base64 = Files.readAllBytes(resourceDirectory.parent.parent.resolve("NA_logo.png")).encodeBase64()

        when:
        def data = source.collect { converter.convert(it) }.grep()
        def webPage = data[0].content
        def xml = new XmlSlurper().parseText(webPage)
        def img = xml.p.img

        then:
        data.size() == 1
        img.@src == "data:image/png;base64,$base64"
    }

}
