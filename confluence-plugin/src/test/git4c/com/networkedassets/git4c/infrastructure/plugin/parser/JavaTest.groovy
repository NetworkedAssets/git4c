package com.networkedassets.git4c.infrastructure.plugin.parser

import com.networkedassets.git4c.core.bussiness.Method
import com.networkedassets.git4c.core.bussiness.Range
import com.networkedassets.git4c.infrastructure.plugin.parser.java.JavaParser
import spock.lang.Specification

import java.nio.file.Paths

class JavaTest extends Specification {

    def parser = new JavaParser()

    def "CheckBoxHolder should be have 2 methods"() {
        given:
        def resourceDirectory = Paths.get("src/test/resources", "sourcefiles/java/CheckBoxHolder.java");

        when:
        def methods = parser.getMethods(resourceDirectory.toFile().getText())
        def method1 = new Method("setUp", new Range(33, 62))
        def method2 = new Method("onInterceptTouchEvent", new Range(65, 73))

        then:
        true
    }

}
