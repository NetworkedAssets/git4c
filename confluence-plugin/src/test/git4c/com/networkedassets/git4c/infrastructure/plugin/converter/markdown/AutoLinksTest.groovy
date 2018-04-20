package com.networkedassets.git4c.infrastructure.plugin.converter.markdown

import com.networkedassets.git4c.core.business.ExtractionResult
import com.networkedassets.git4c.core.business.Macro
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

import static com.networkedassets.git4c.infrastructure.plugin.converter.ConverterUtils.getConvertedMarkdown

class AutoLinksTest extends Specification {

    def "Links inside a tags shouldn't be changed whole flow in Multi File Macro"() {

        given:
        Path resultFile = Paths.get("src/test/resources", "markdown/autoLinks/Test1/result.html")
        def webPage = getConvertedMarkdown("autoLinks/Test1", Macro.MacroType.MULTIFILE).content
        def result = resultFile.getText("UTF-8")

        when:
        def xml = new XmlParser().parseText(webPage)
        def xml2 = new XmlParser().parseText(result)

        then:
        xml.toString() == xml2.toString()

    }


    def "Links outside a tags should be wrapped whole flow in Multi File Macro"() {

        given:
        Path resultFile = Paths.get("src/test/resources", "markdown/autoLinks/Test2/result.html")
        def webPage = getConvertedMarkdown("autoLinks/Test2", Macro.MacroType.MULTIFILE).content
        def result = resultFile.getText("UTF-8")

        when:
        def xml = new XmlParser().parseText(webPage)
        def xml2 = new XmlParser().parseText(result)

        then:
        xml.toString() == xml2.toString()

    }

    def "Links inside a tags shouldn't be changed whole flow in Single File Macro"() {

        given:
        Path resultFile = Paths.get("src/test/resources", "markdown/autoLinks/Test1/result.html")
        def webPage = getConvertedMarkdown("autoLinks/Test1", Macro.MacroType.SINGLEFILE).content
        def result = resultFile.getText("UTF-8")

        when:
        def xml = new XmlParser().parseText(webPage)
        def xml2 = new XmlParser().parseText(result)

        then:
        xml.toString() == xml2.toString()

    }


    def "Links outside a tags should be wrapped whole flow in Single File Macro"() {

        given:
        Path resultFile = Paths.get("src/test/resources", "markdown/autoLinks/Test2/result.html")
        def webPage = getConvertedMarkdown("autoLinks/Test2", Macro.MacroType.SINGLEFILE).content
        def result = resultFile.getText("UTF-8")

        when:
        def xml = new XmlParser().parseText(webPage)
        def xml2 = new XmlParser().parseText(result)

        then:
        xml.toString() == xml2.toString()

    }

}
