package com.networkedassets.git4c.infrastructure.pageManager

import com.networkedassets.git4c.core.business.Macro
import com.networkedassets.git4c.infrastructure.AtlassianPageMacroExtractor
import spock.lang.Specification

import java.nio.file.Paths

import static com.networkedassets.git4c.core.business.Macro.MacroType.*

class AtlassianPageMacroExtractionTest extends Specification {

    def "Macro data from page extraction test"() {

        given:
        def pageSource = Paths.get("src/test/resources", "pageExtractorTest/site.xml").toFile().text
        def extractor = new AtlassianPageMacroExtractor()
        def expected = [new Macro("asd", SINGLEFILE), new Macro("d45521e284eb4289b404624e04c8e6c6", SINGLEFILE), new Macro("301b8539f91e4b3aa9becc3531eaf609", SINGLEFILE), new Macro("53e2605959aa472a9bcd8d28b043dd34", MULTIFILE)]

        when:
        def page = extractor.extractMacro(pageSource)

        then:
        expected.sort() == page.sort()

    }

}
