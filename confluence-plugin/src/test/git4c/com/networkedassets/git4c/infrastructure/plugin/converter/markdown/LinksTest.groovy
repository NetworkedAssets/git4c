package com.networkedassets.git4c.infrastructure.plugin.converter.markdown

import spock.lang.Specification

import java.nio.file.Paths

import static com.networkedassets.git4c.test.Utils.getDataFromDirectory

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
        def resourceDirectory = Paths.get("src/test/resources", "linksTest/relative/Markdown1.md");
        def source = getDataFromDirectory(resourceDirectory.parent)
        def converter = new MarkdownConverterPlugin()

        when:
        def data = source.collect {
            converter.convert(it)
        }.grep()
        def webPage = data.stream().filter({ it.path == "Markdown1.md" }).findAny().get().content
        def xml = new XmlSlurper().parseText("""<span xmlns:v-on="http://www.w3.org/1999/xhtml">${webPage}</span>""")
        def a0 = xml.span.p.a[0]
        def a1 = xml.span.p.a[1]

        then:
        a0.@href == "javascript:void(0)"
        a0.@"v-on:click" == "moveToFile('subfolder%2FMarkdown2.md')"
        a1.@href == "javascript:void(0)"
        a1.@"v-on:click" == ""
        a1.@class.toString().split(' ').contains("git4c-unclickable-link")
    }

}
