package com.networkedassets.git4c.infrastructure.plugin.converter.markdown

import com.networkedassets.git4c.core.business.Macro
import spock.lang.Specification

import static com.networkedassets.git4c.infrastructure.plugin.converter.ConverterUtils.getConvertedMarkdown

class ImageToLinkTest extends Specification {

    def "Image links that don't point to images are converted to links in Multi File Macro"() {

        given:
        def webPage = getConvertedMarkdown("imageToLinkTest", Macro.MacroType.MULTIFILE).content

        when:
        //Check if it's proper xml
        new XmlSlurper().parseText("""<span xmlns:v-on="http://www.w3.org/1999/xhtml">${webPage}</span>""")

        then:
        webPage == """<span><p><a href="javascript:void(0)" v-on:click="moveToFile('README', '')">link</a></p> </span>"""
    }

    def "Image links that don't point to images are converted to text in Single File Macro"() {

        given:
        def webPage = getConvertedMarkdown("imageToLinkTest", Macro.MacroType.SINGLEFILE).content

        when:
        //Check if it's proper xml
        new XmlSlurper().parseText("""<span xmlns:v-on="http://www.w3.org/1999/xhtml">${webPage}</span>""")

        then:
        webPage == """<span><p><a href="javascript:void(0)" class=" git4c-image git4c-unclickable-link">link</a></p> </span>"""
    }

}
