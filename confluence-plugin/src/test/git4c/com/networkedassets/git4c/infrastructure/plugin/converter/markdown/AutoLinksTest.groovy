package com.networkedassets.git4c.infrastructure.plugin.converter.markdown

import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

import static com.networkedassets.git4c.test.Utils.getDataFromDirectory

class AutoLinksTest extends Specification {


    def "Inline text url should be changed to link html tag"() {

        given:
        def converter = new MarkdownConverterPlugin()
        def source = """<span>Linki do stron: <b><span>Text before</span>Google: https://google.pl Yahoo: yahoo.com <span>SPAN</span></b> Text po</span>"""

        when:
        def html = converter.parseLinks(source)

        then:
        html == """<span>Linki do stron: <b><span>Text before</span>Google: <a href="https://google.pl">https://google.pl</a> Yahoo: <a href="yahoo.com">yahoo.com</a> <span>SPAN</span></b> Text po</span>"""


    }

    def "a links shouldn't be changed"() {

        given:
        def converter = new MarkdownConverterPlugin()
        def source = """<a href="reddit.com">https://google.com</a>"""

        when:
        def html = converter.parseLinks(source)

        then:
        html == source

    }


    def "http links should work properly"(String url) {

        given:
        def converter = new MarkdownConverterPlugin()
        def source = """<p>$url</p>"""

        when:
        def html = converter.parseLinks(source)

        then:
        html == """<p><a href="$url">$url</a></p>"""

        where:
        url                                        | _
        "http://google.com"                        | _
        "http://{{variable}}.google.com"           | _
        "http://pc-aressel:1990/confluence"        | _
        "https://{hostname}/screens/frameset.html" | _
        "https://{hostname}:8443/wsg/"             | _
        "https://{hostname}:7443/api/Carrier.html" | _
    }

    def "Links inside a tags shouldn't be changed whole flow"() {

        given:
        Path resourceDirectory = Paths.get("src/test/resources", "autoLinks/Test1/Markdown.md")
        Path resultFile = Paths.get("src/test/resources", "autoLinks/Test1/result.html")

        def result = resultFile.getText("UTF-8")

        def source = getDataFromDirectory(resourceDirectory.parent)

        def converter = new MarkdownConverterPlugin()

        when:
        def data = source.collect { converter.convert(it) }.grep()
        def webPage = data[0].content
        def xml = new XmlParser().parseText(webPage)
        def xml2 = new XmlParser().parseText(result)

        then:
        xml.toString() == xml2.toString()

    }


    def "Links outside a tags should be wrapped whole flow"() {

        given:
        Path resourceDirectory = Paths.get("src/test/resources", "autoLinks/Test2/Markdown.md")
        Path resultFile = Paths.get("src/test/resources", "autoLinks/Test2/result.html")

        def result = resultFile.getText("UTF-8")

        def source = getDataFromDirectory(resourceDirectory.parent)

        def converter = new MarkdownConverterPlugin()

        when:
        def data = source.collect { converter.convert(it) }.grep()
        def webPage = data[0].content
        def xml = new XmlParser().parseText(webPage)
        def xml2 = new XmlParser().parseText(result)

        then:
        xml.toString() == xml2.toString()

    }

}
