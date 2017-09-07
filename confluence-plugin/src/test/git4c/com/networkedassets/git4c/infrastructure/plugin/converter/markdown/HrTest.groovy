package com.networkedassets.git4c.infrastructure.plugin.converter.markdown

import spock.lang.Specification

import java.nio.file.Paths

import static com.networkedassets.git4c.test.Utils.getDataFromDirectory

class HrTest extends Specification {

    def "hr tag should work (shouldn't throw exception)"() {
        given:
        def resourceDirectory = Paths.get("src/test/resources", "hrtest/Markdown.md");
        def source = getDataFromDirectory(resourceDirectory.parent)
        def converter = new MarkdownConverterPlugin()

        when:
        def data = source.collect { converter.convert(it) }

        then:
        data.size() == 1
    }

}
