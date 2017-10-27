package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.core.bussiness.ParserPlugin
import com.networkedassets.git4c.core.datastore.extractors.LineNumbersExtractorData
import org.mockito.Mockito
import spock.lang.Specification

import java.nio.file.Paths

class ExtractContentProcessTest extends Specification {

    def "ExtractContentProcess lines test"() {

        given:
        def process = new ExtractContentProcess(Mockito.mock(ParserPlugin.class))
        def resourceDirectory = Paths.get("src/test/resources", "sourcefiles/gherkin/file2.feature");

        when:
        def file = new ImportedFileData("", resourceDirectory, {""}, {""}, {new Date()}, { resourceDirectory.toFile().text.bytes })

        def result = process.extract(new LineNumbersExtractorData("a", 5, 11), file)

        def expected = """    And a blog named "Greg's anti-tax rants"
    And a customer named "Wilson"
    And a blog named "Expensive Therapy" owned by "Wilson"

  Scenario: Wilson posts to his own blog
    Given I am logged in as Wilson
    When I try to post to "Expensive Therapy\""""

        then:
        result.content == expected


    }

}