package com.networkedassets.git4c.infrastructure.plugin.parser

import com.networkedassets.git4c.core.bussiness.Method
import com.networkedassets.git4c.core.bussiness.Range
import com.networkedassets.git4c.infrastructure.plugin.parser.gherkin.GherkinParser
import spock.lang.Specification

import java.nio.file.Paths

class GherkinTest extends Specification {

    def parser = new GherkinParser()

    def "Get features from files with single feature"() {
        given:
        def resourceDirectory = Paths.get("src/test/resources", "sourcefiles/gherkin/file1.feature");

        when:
        def range = parser.getMethods(resourceDirectory.toFile().getText())

        then:
        [new Method("Buy last coffee", new Range(6, 10))] == range

    }

    def "Get features from files with multiple features"() {
        given:
        def resourceDirectory = Paths.get("src/test/resources", "sourcefiles/gherkin/file2.feature");

        when:
        def range = parser.getMethods(resourceDirectory.toFile().getText())

        then:
        def method1 = new Method("Wilson posts to his own blog", new Range(9, 12))
        def method2 = new Method("Greg posts to a client's blog", new Range(14, 17))
        [method1, method2] == range

    }

}
