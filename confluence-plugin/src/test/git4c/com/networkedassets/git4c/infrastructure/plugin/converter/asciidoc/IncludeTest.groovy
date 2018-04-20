package com.networkedassets.git4c.infrastructure.plugin.converter.asciidoc

import com.networkedassets.git4c.core.business.Macro
import com.networkedassets.git4c.infrastructure.plugin.converter.ConverterUtils
import spock.lang.Specification

import java.nio.file.Paths

import static com.networkedassets.git4c.infrastructure.plugin.converter.ConverterUtils.getConvertedAsciidoc
import static com.networkedassets.git4c.test.Utils.getDataFromDirectory

class IncludeTest extends Specification {

    def "include tag works in Asciidoc in Multi File Macro"() {

        given:
        def webPage = getConvertedAsciidoc("includeTest/subfolder", Macro.MacroType.MULTIFILE).content

        expect:
        webPage.contains("password")

    }

    def "include tags works from parent directories in Multi File Macro"() {

        given:
        def webPage = getConvertedAsciidoc("includeTest/parentfolder", Macro.MacroType.MULTIFILE).content

        expect:
        webPage.contains("password")

    }

    def "include tag won't include anything from outside of repository in Multi File Macro"() {

        given:
        def webPage = getConvertedAsciidoc("includeTest/outsidefolder", Macro.MacroType.MULTIFILE).content

        expect:
        !webPage.contains("password")

    }

    def "include tag works in Asciidoc in Single File Macro"() {

        given:
        def webPage = getConvertedAsciidoc("includeTest/subfolder", Macro.MacroType.SINGLEFILE).content

        expect:
        webPage.contains("password")

    }

    def "include tags works from parent directories in Single File Macro"() {

        given:
        def webPage = getConvertedAsciidoc("includeTest/parentfolder", Macro.MacroType.SINGLEFILE).content

        expect:
        webPage.contains("password")

    }

    def "include tag won't include anything from outside of repository in Single File Macro"() {

        given:
        def webPage = getConvertedAsciidoc("includeTest/outsidefolder", Macro.MacroType.SINGLEFILE).content

        expect:
        !webPage.contains("password")

    }

    def "Files to remove test"() {

        given:
        def resourceDirectory = Paths.get("src/test/resources", "asciidoc/includeTest/transitiveincludefolder");
        def source = getDataFromDirectory(resourceDirectory)
        def file = source.find { it.path == "a.adoc" }
        def toIgnore = ConverterUtils.asciidocConverter.getFilesToIgnore(file)

        expect:
        toIgnore == ["include/b.adoc", "include/c.adoc"]
    }

    def "Files to remove parent test"() {

        given:
        def resourceDirectory = Paths.get("src/test/resources", "asciidoc/includeTest/includeadocparent");
        def source = getDataFromDirectory(resourceDirectory)
        def file = source.find { it.path == "a/a.adoc" }
        def toIgnore = ConverterUtils.asciidocConverter.getFilesToIgnore(file)

        expect:
        toIgnore == ["b/b.adoc"]


    }

}
