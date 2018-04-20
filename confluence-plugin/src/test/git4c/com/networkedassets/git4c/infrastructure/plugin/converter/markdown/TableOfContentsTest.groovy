package com.networkedassets.git4c.infrastructure.plugin.converter.markdown

import com.networkedassets.git4c.core.business.ExtractionResult
import com.networkedassets.git4c.core.business.Macro
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.data.macro.documents.item.TableOfContents
import spock.lang.Specification

import static com.networkedassets.git4c.infrastructure.plugin.converter.ConverterUtils.getConvertedMarkdown

class TableOfContentsTest extends Specification {

    def "Table of Content is generated in Multi File Macro"() {

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

        def source = getConvertedMarkdown("tableOfContents", Macro.MacroType.MULTIFILE)

        when:
        def table = source.tableOfContents

        then:
        table == expected
    }

    def "Table of Content is generated in Single File Macro"() {

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

        def source = getConvertedMarkdown("tableOfContents", Macro.MacroType.SINGLEFILE)

        when:
        def table = source.tableOfContents

        then:
        table == expected
    }

}
