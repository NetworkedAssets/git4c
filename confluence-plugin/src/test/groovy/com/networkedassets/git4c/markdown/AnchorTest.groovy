package com.networkedassets.git4c.markdown

import com.networkedassets.git4c.infrastructure.plugin.converter.markdown.MarkdownConverterPlugin
import spock.lang.Specification

import java.nio.file.Paths

import static com.networkedassets.git4c.Utils.getDataFromDirectory

class AnchorTest extends Specification {

    def "Anchors should be parsed properly"() {

        given:
        def resourceDirectory = Paths.get("src/test/resources", "anchorTest/Markdown.md");
        def source = getDataFromDirectory(resourceDirectory.parent)
        def converter = new MarkdownConverterPlugin()

        when:
        def data = source.collect { converter.convert(it) }
        def webPage = data[0].content
        def webPageWithNamespace = webPage.replaceFirst("<p", """<p xmlns:v-on="http://www.w3.org/1999/xhtml" """)
        def xml = new XmlSlurper().parseText(webPageWithNamespace)
        def a = xml.p.a

        then:
        data.size() == 1
        a.@href == "javascript:void(0)"
        a.@"v-on:click" == "anchor('r1')"
    }

}
