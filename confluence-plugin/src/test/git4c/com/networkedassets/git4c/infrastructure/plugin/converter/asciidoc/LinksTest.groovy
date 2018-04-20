package com.networkedassets.git4c.infrastructure.plugin.converter.asciidoc

import com.networkedassets.git4c.core.business.Macro
import spock.lang.Specification

import static com.networkedassets.git4c.infrastructure.plugin.converter.ConverterUtils.getConvertedAsciidoc

class LinksTest extends Specification {

    def "http(s) links should remain the same in Multi File Macro"() {
        given:
        def webPage = getConvertedAsciidoc("linksTest/http", Macro.MacroType.MULTIFILE).content

        when:
        def xml = new XmlSlurper().parseText(webPage)
        def a = xml.p.a

        then:
        a.@href == "https://www.google.com"
    }

    def "Relative links should be replaced with anchors in Multi File Macro"() {

        given:
        def webPage = getConvertedAsciidoc("linksTest/relative", Macro.MacroType.MULTIFILE).content

        when:
        def xml = new XmlSlurper().parseText("""<span xmlns:v-on="http://www.w3.org/1999/xhtml">${webPage}</span>""")
        def a0 = xml.div.p.a[0]
        def a1 = xml.div.p.a[1]

        then:
        a0.@href == "javascript:void(0)"
        a0.@"v-on:click" == "moveToFile('subfolder%2FMarkdown2.md', '')"
        a1.@href == "javascript:void(0)"
        a1.@"v-on:click" == ""
        a1.@class.toString().contains("git4c-unclickable-link")
    }

    def "http(s) links should remain the same in Single File Macro"() {
        given:
        def webPage = getConvertedAsciidoc("linksTest/http", Macro.MacroType.SINGLEFILE).content

        when:
        def xml = new XmlSlurper().parseText(webPage)
        def a = xml.p.a

        then:
        a.@href == "https://www.google.com"
    }

    def "Relative links should be replaced with text in Single File Macro"() {

        given:
        def webPage = getConvertedAsciidoc("linksTest/relative", Macro.MacroType.SINGLEFILE).content

        when:
        def xml = new XmlSlurper().parseText("""<span xmlns:v-on="http://www.w3.org/1999/xhtml">${webPage}</span>""")
        def a0 = xml.div.p.a[0]
        def a1 = xml.div.p.a[1]

        then:
        a0.@href == "javascript:void(0)"
        a0.@"v-on:click" != "moveToFile('subfolder%2FMarkdown2.md', '')"
        a1.@href == "javascript:void(0)"
        a1.@"v-on:click" == ""
        a1.@class.toString().contains("git4c-unclickable-link")
    }

}
