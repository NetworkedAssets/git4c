package com.networkedassets.git4c.infrastructure.plugin.converter.markdown

import com.networkedassets.git4c.core.business.Macro
import spock.lang.Specification

import static com.networkedassets.git4c.infrastructure.plugin.converter.ConverterUtils.getConvertedMarkdown

class AnchorTest extends Specification {

    def "Anchors should be parsed properly in Multi File Macro"() {

        given:
        def webPage = getConvertedMarkdown("anchorTest", Macro.MacroType.MULTIFILE).content

        when:
        def webPageWithNamespace = webPage.replaceFirst("<p", """<p xmlns:v-on="http://www.w3.org/1999/xhtml" """)
        def xml = new XmlSlurper().parseText(webPageWithNamespace)
        def a = xml.p.a

        then:
        a == "Rozdzial 1"
        a.@href == "javascript:void(0)"
        a.@"v-on:click" == "anchor('r1')"
    }

    def "Anchors should be parsed properly in Single File Macro"() {

        given:
        def webPage = getConvertedMarkdown("anchorTest", Macro.MacroType.SINGLEFILE).content

        when:
        def webPageWithNamespace = webPage.replaceFirst("<p", """<p xmlns:v-on="http://www.w3.org/1999/xhtml" """)
        def xml = new XmlSlurper().parseText(webPageWithNamespace)
        def a = xml.p.a

        then:
        a == "Rozdzial 1"
        a.@href == "javascript:void(0)"
        a.@"v-on:click" == "anchor('r1')"
    }
}
