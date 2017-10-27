package com.networkedassets.git4c.infrastructure.plugin.converter.markdown

import com.networkedassets.git4c.core.business.ExtractionResult
import spock.lang.Specification

import static com.networkedassets.git4c.infrastructure.plugin.converter.ConverterUtils.getMarkdown

class HrTest extends Specification {

    def "hr tag should work (shouldn't throw exception)"() {
        when:
        def text = getMarkdown("hrtest")

        then:
        text
    }

}
