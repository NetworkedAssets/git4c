package com.networkedassets.git4c.infrastructure.plugin.converter.markdown

import spock.lang.Specification

import static com.networkedassets.git4c.infrastructure.plugin.converter.ConverterUtils.getMarkdown

class LinksTest extends Specification {

    def "http(s) links should remain the same"() {
        given:
        def webPage = getMarkdown("linksTest/http").content

        when:
        def xml = new XmlSlurper().parseText(webPage)
        def a = xml.p.a

        then:
        a.@href == "https://www.google.com"
    }

    def "Relative links should be replaced with anchors"() {

        given:
        def webPage = getMarkdown("linksTest/relative")

        when:
        def xml = new XmlSlurper().parseText("""<span xmlns:v-on="http://www.w3.org/1999/xhtml">${webPage}</span>""")
        def a0 = xml.span.p.a[0]
        def a1 = xml.span.p.a[1]
        def a2 = xml.span.p.a[2]
        def a3 = xml.span.p.a[3]

        then:
        a0.@href == "javascript:void(0)"
        a0.@"v-on:click" == "moveToFile('subfolder%2Ffile2.txt', '')"
        a1.@href == "javascript:void(0)"
        a1.@"v-on:click" == ""
        a1.@class.toString().split(' ').contains("git4c-unclickable-link")
        a2.@href == "javascript:void(0)"
        a2.@"v-on:click" == "moveToFile('subfolder%2Ffile2.txt', 'secondparagraph')"
        a3.@href == "javascript:void(0)"
        a3.@"v-on:click" == "moveToFile('subfolder%2Ffile%20with%20spaces.txt', '')"
    }

}
