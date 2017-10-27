package com.networkedassets.git4c.infrastructure.plugin.converter.markdown

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

import static com.networkedassets.git4c.infrastructure.plugin.converter.ConverterUtils.getMarkdown

class ImageTest extends Specification {

    def "Http(s) links shouldn't be altered"() {

        given:
        def webPage = getMarkdown("imageTest/httpImage").content

        when:
        def xml = new XmlSlurper().parseText(webPage)
        def img = xml.p.img

        then:
        img.@src == "https://octodex.github.com/images/yaktocat.png"
    }

    def "Png images in subfolders should be converted to Base64"() {

        given:
        def resourceDirectory = Paths.get("src/test/resources", "markdown/imageTest/relativeImage/Markdown.md");
        def base64 = Files.readAllBytes(resourceDirectory.parent.resolve("img/NA_logo.png")).encodeBase64()
        def webPage = getMarkdown("imageTest/relativeImage").content

        when:
        def xml = new XmlSlurper().parseText(webPage)
        def img = xml.p.img

        then:
        img.@src == "data:image/png;base64,$base64"
    }

    def "Svg images in subfolders should be converted to Base64"() {

        given:
        def resourceDirectory = Paths.get("src/test/resources", "markdown/imageTest/relativesvgImage/Markdown.md");
        def base64 = Files.readAllBytes(resourceDirectory.parent.resolve("img/circle.svg")).encodeBase64()
        def webPage = getMarkdown("imageTest/relativesvgImage").content

        when:
        def xml = new XmlSlurper().parseText(webPage)
        def img = xml.p.img

        then:
        img.@src == "data:image/svg+xml;base64,$base64"
    }

    def "Images in parent folders should also work"() {

        given:
        def resourceDirectory = Paths.get("src/test/resources", "markdown/imageTest/parentImage/markdown/Markdown.md");
        def base64 = Files.readAllBytes(resourceDirectory.parent.parent.resolve("NA_logo.png")).encodeBase64()

        def webPage = getMarkdown("imageTest/parentImage").content

        when:
        def xml = new XmlSlurper().parseText(webPage)
        def img = xml.p.img

        then:
        img.@src == "data:image/png;base64,$base64"
    }

}
