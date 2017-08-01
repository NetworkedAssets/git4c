package com.networkedassets.git4c.markdown

import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.data.macro.documents.item.TableOfContents
import com.networkedassets.git4c.infrastructure.plugin.converter.markdown.MarkdownConverterPlugin
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

import static com.networkedassets.git4c.Utils.getDataFromDirectory

class TableOfContentsTest extends Specification {

    class UUIDGen implements IdentifierGenerator {

        private int i = 0

        @Override
        String generateNewIdentifier() {
            i++
            return i
        }
    }

    def "Basic test passes"() {

        given:
        Path resourceDirectory = Paths.get("src/test/resources", "tableOfContents/Markdown.md")

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

        def source = getDataFromDirectory(resourceDirectory.parent)

        def converter = new MarkdownConverterPlugin()
        converter.generator = new UUIDGen()

        when:
        def data = source.collect { converter.convert(it) }.grep()
        def table = data[0].tableOfContents
//        def webPage = data[0].content
//        def xml = new XmlParser().parseText("""<span xmlns:v-on="http://www.w3.org/1999/xhtml">$webPage</span>""")
//        def xml2 = new XmlParser().parseText(result)

        then:
        table == expected
    }

}
