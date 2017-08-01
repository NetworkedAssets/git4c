package com.networkedassets.git4c.markdown

import com.networkedassets.git4c.infrastructure.plugin.converter.markdown.MarkdownConverterPlugin
import spock.lang.Specification

import java.nio.file.Paths

import static com.networkedassets.git4c.Utils.getDataFromDirectory

class LinksTest extends Specification {

    def "http(s) links should remain the same"() {
        given:
        def resourceDirectory = Paths.get("src/test/resources", "linksTest/http/Markdown.md");
        def source = getDataFromDirectory(resourceDirectory.parent)
        def converter = new MarkdownConverterPlugin()

        when:
        def data = source.collect { converter.convert(it) }.grep()
        def webPage = data[0].content
        def xml = new XmlSlurper().parseText(webPage)
        def a = xml.p.a

        then:
        a.@href == "https://www.google.com"
    }

    def "Relative links should be replaced with anchors"() {

        given:
        def resourceDirectory = Paths.get("src/test/resources", "linksTest/relative/Markdown.md");
        def source = getDataFromDirectory(resourceDirectory.parent)
        def converter = new MarkdownConverterPlugin()

        when:
        def data = source.collect { converter.convert(it) }.grep()
        def webPage = data[0].content
        def xml = new XmlSlurper().parseText(webPage)
        def a = xml.p.a

        then:
        a.@href == "#/anotherFolder%2Ffile.md"
    }

}
