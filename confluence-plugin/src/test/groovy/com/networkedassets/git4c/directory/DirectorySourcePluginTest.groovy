package com.networkedassets.git4c.directory

import spock.lang.Specification

import java.nio.file.Paths

import static com.networkedassets.git4c.Utils.getDataFromDirectory

class DirectorySourcePluginTest extends Specification {

    def "Files are properly read"() {

        given:
        def resourceDirectory = Paths.get("src/test/resources", "directoryPlugin/test1");

        when:
        def data = getDataFromDirectory(resourceDirectory)

        then:
        //TODO: Finish this test
        data.size() == 2
    }


}
