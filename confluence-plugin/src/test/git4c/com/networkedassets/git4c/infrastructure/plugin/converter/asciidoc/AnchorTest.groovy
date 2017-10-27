package com.networkedassets.git4c.infrastructure.plugin.converter.asciidoc

import spock.lang.Specification

import static com.networkedassets.git4c.infrastructure.plugin.converter.ConverterUtils.getAsciidoc

class AnchorTest extends Specification {

    def "Anchors should be parsed properly"() {

        given:
        def webPage = getAsciidoc("anchorTest").content

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
