package com.networkedassets.git4c.infrastructure.plugin.converter.markdown

import spock.lang.Specification

import static com.networkedassets.git4c.infrastructure.plugin.converter.ConverterUtils.getMarkdown

class ImageToLinkTest extends Specification {

    def "Image links that don't point to images are converted to links"() {

        given:
        def webPage = getMarkdown("imageToLinkTest").content

        when:
        //Check if it's proper xml
        new XmlSlurper().parseText("""<span xmlns:v-on="http://www.w3.org/1999/xhtml">${webPage}</span>""")

        then:
        webPage == """<span><p><a href="javascript:void(0)" v-on:click="moveToFile('README', '')">link</a></p> </span>"""
    }


}
