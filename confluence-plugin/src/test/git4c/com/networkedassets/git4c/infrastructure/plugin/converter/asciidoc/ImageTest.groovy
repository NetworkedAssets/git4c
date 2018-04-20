package com.networkedassets.git4c.infrastructure.plugin.converter.asciidoc

import com.networkedassets.git4c.core.business.Macro
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

import static com.networkedassets.git4c.infrastructure.plugin.converter.ConverterUtils.getConvertedAsciidoc

class ImageTest extends Specification {

    def "Http(s) links shouldn't be altered in Multi File Macro"() {

        given:
        def webPage = getConvertedAsciidoc("imageTest/httpImage/", Macro.MacroType.MULTIFILE).content

        when:
        def xml = new XmlSlurper().parseText(webPage)
        def img = xml.p.span.img

        then:
        img.@src == "https://octodex.github.com/images/yaktocat.png"
    }

    def "Png images in subfolders should be converted to Base64 in Multi File Macro"() {

        given:
        def resourceDirectory = Paths.get("src/test/resources", "asciidoc/imageTest/relativeImage/asciidoc.adoc");
        def base64 = Files.readAllBytes(resourceDirectory.parent.resolve("img/NA_logo.png")).encodeBase64()
        def webPage = getConvertedAsciidoc("imageTest/relativeImage", Macro.MacroType.MULTIFILE).content

        when:
        def xml = new XmlSlurper().parseText(webPage)
        def img = xml.p.span.img

        then:
        img.@src == "data:image/png;base64,$base64"
    }

    def "Svg images in subfolders should be converted to Base64 in Multi File Macro"() {

        given:
        def resourceDirectory = Paths.get("src/test/resources", "asciidoc/imageTest/relativesvgImage/asciidoc.adoc");
        def base64 = Files.readAllBytes(resourceDirectory.parent.resolve("img/circle.svg")).encodeBase64()
        def webPage = getConvertedAsciidoc("imageTest/relativesvgImage", Macro.MacroType.MULTIFILE).content

        when:
        def xml = new XmlSlurper().parseText(webPage)
        def img = xml.p.span.img

        then:
        img.@src == "data:image/svg+xml;base64,$base64"
    }

    def "Images in parent folders should also work in Multi File Macro"() {

        given:
        def resourceDirectory = Paths.get("src/test/resources", "asciidoc/imageTest/parentImage/markdown/asciidoc.adoc");
        def base64 = Files.readAllBytes(resourceDirectory.parent.parent.resolve("NA_logo.png")).encodeBase64()
        def webPage = getConvertedAsciidoc("imageTest/parentImage", Macro.MacroType.MULTIFILE).content

        when:
        def xml = new XmlSlurper().parseText(webPage)
        def img = xml.p.span.img

        then:
        img.@src == "data:image/png;base64,$base64"
    }

    def "Http(s) links shouldn't be altered in Single File Macro"() {

        given:
        def webPage = getConvertedAsciidoc("imageTest/httpImage/", Macro.MacroType.SINGLEFILE).content

        when:
        def xml = new XmlSlurper().parseText(webPage)
        def img = xml.p.span.img

        then:
        img.@src == "https://octodex.github.com/images/yaktocat.png"
    }

    def "Png images in subfolders should be converted to Base64 in Single File Macro"() {

        given:
        def resourceDirectory = Paths.get("src/test/resources", "asciidoc/imageTest/relativeImage/asciidoc.adoc");
        def base64 = Files.readAllBytes(resourceDirectory.parent.resolve("img/NA_logo.png")).encodeBase64()
        def webPage = getConvertedAsciidoc("imageTest/relativeImage", Macro.MacroType.SINGLEFILE).content

        when:
        def xml = new XmlSlurper().parseText(webPage)
        def img = xml.p.span.img

        then:
        img.@src == "data:image/png;base64,$base64"
    }

    def "Svg images in subfolders should be converted to Base64 in Single File Macro"() {

        given:
        def resourceDirectory = Paths.get("src/test/resources", "asciidoc/imageTest/relativesvgImage/asciidoc.adoc");
        def base64 = Files.readAllBytes(resourceDirectory.parent.resolve("img/circle.svg")).encodeBase64()
        def webPage = getConvertedAsciidoc("imageTest/relativesvgImage", Macro.MacroType.SINGLEFILE).content

        when:
        def xml = new XmlSlurper().parseText(webPage)
        def img = xml.p.span.img

        then:
        img.@src == "data:image/svg+xml;base64,$base64"
    }

    def "Images in parent folders should also work in Single File Macro"() {

        given:
        def resourceDirectory = Paths.get("src/test/resources", "asciidoc/imageTest/parentImage/markdown/asciidoc.adoc");
        def base64 = Files.readAllBytes(resourceDirectory.parent.parent.resolve("NA_logo.png")).encodeBase64()
        def webPage = getConvertedAsciidoc("imageTest/parentImage", Macro.MacroType.SINGLEFILE).content

        when:
        def xml = new XmlSlurper().parseText(webPage)
        def img = xml.p.span.img

        then:
        img.@src == "data:image/png;base64,$base64"
    }
}
