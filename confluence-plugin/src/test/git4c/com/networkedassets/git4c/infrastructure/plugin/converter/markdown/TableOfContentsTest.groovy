package com.networkedassets.git4c.infrastructure.plugin.converter.markdown

import com.networkedassets.git4c.core.business.ExtractionResult
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.data.macro.documents.item.TableOfContents
import spock.lang.Specification

import static com.networkedassets.git4c.infrastructure.plugin.converter.ConverterUtils.getMarkdown

class TableOfContentsTest extends Specification {

    def "Basic test passes"() {

        given:
        def expected = new TableOfContents("", "", [
                new TableOfContents("Heading 1 (--dev-config)", "1", [
                        new TableOfContents("Subheading 1.1 (&lt;code&gt;)", "2", [
                                new TableOfContents("Subheading 1.1.1", "3", [])
                        ]),
                        new TableOfContents("Subheading 1.2", "4", [])
                ]),
                new TableOfContents("Heading 2", "5", [
                        new TableOfContents("Subheading 2.1", "6", [])
                ]),
                new TableOfContents("Heading 3", "7", [
                        new TableOfContents("Subheading 3.1", "8", [
                                new TableOfContents("Subheading 3.1.1", "9", [])
                        ])
                ])
        ])

        def source = getMarkdown("tableOfContents")

        when:
        def table = source.tableOfContents

        then:
        table == expected
    }

}
