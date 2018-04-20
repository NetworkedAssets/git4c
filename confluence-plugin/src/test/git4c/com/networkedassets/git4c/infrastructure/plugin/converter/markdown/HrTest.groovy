package com.networkedassets.git4c.infrastructure.plugin.converter.markdown

import com.networkedassets.git4c.core.business.ExtractionResult
import com.networkedassets.git4c.core.business.Macro
import spock.lang.Specification

import static com.networkedassets.git4c.infrastructure.plugin.converter.ConverterUtils.getConvertedMarkdown

class HrTest extends Specification {

    def "hr tag should work (shouldn't throw exception) in Multi File Macro"() {
        when:
        def text = getConvertedMarkdown("hrtest", Macro.MacroType.MULTIFILE)

        then:
        text
    }

    def "hr tag should work (shouldn't throw exception) in Single File Macro"() {
        when:
        def text = getConvertedMarkdown("hrtest", Macro.MacroType.SINGLEFILE)

        then:
        text
    }

}
