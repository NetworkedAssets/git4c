package com.networkedassets.git4c.infrastructure.plugin.converter.markdown

import com.networkedassets.git4c.core.business.ExtractionResult
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

import static com.networkedassets.git4c.infrastructure.plugin.converter.ConverterUtils.getMarkdown

class AutoLinksTest extends Specification {

    def "Links inside a tags shouldn't be changed whole flow"() {

        given:
        Path resultFile = Paths.get("src/test/resources", "markdown/autoLinks/Test1/result.html")
        def webPage = getMarkdown("autoLinks/Test1").content
        def result = resultFile.getText("UTF-8")

        when:
        def xml = new XmlParser().parseText(webPage)
        def xml2 = new XmlParser().parseText(result)

        then:
        xml.toString() == xml2.toString()

    }


    def "Links outside a tags should be wrapped whole flow"() {

        given:
        Path resultFile = Paths.get("src/test/resources", "markdown/autoLinks/Test2/result.html")
        def webPage = getMarkdown("autoLinks/Test2").content
        def result = resultFile.getText("UTF-8")

        when:
        def xml = new XmlParser().parseText(webPage)
        def xml2 = new XmlParser().parseText(result)

        then:
        xml.toString() == xml2.toString()

    }

}
