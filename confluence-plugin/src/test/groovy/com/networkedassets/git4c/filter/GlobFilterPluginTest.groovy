package com.networkedassets.git4c.filter

import com.networkedassets.git4c.infrastructure.plugin.filter.GlobFilterPlugin
import spock.lang.Specification

import java.nio.file.Paths

import static com.networkedassets.git4c.Utils.getDataFromDirectory


class GlobFilterPluginTest extends Specification {

    def "everything glob test"() {

        given:
        def filter = new GlobFilterPlugin(GlobFilterPlugin.EVERYTHING)

        def resourceDirectory = Paths.get("src/test/resources", "globTest")
        def files = getDataFromDirectory(resourceDirectory)

        when:
        def files2 = files.findAll { filter.filter(it) }

        then:
        files == files2
    }

    def 'null glob test'() {

        given:
        def filter = new GlobFilterPlugin("")

        def resourceDirectory = Paths.get("src/test/resources", "globTest")
        def files = getDataFromDirectory(resourceDirectory)

        when:
        def files2 = files.findAll { filter.filter(it) }

        then:
        files == files2

    }

    def 'top level glob test'() {
        given:
        def filter = new GlobFilterPlugin("*.*")

        def resourceDirectory = Paths.get("src/test/resources", "globTest")
        def files = getDataFromDirectory(resourceDirectory)

        when:
        def files2 = files.findAll { filter.filter(it) }

        then:
        files2.collect {it.path}.sort() == ["file1.md"].sort()

    }

    def "one level in subfolder test"() {
        given:
        def filter = new GlobFilterPlugin("subfolder/*")

        def resourceDirectory = Paths.get("src/test/resources", "globTest")
        def files = getDataFromDirectory(resourceDirectory)

        when:
        def files2 = files.findAll { filter.filter(it) }

        then:
        files2.collect {it.path}.sort() == ["subfolder/file2.md", "subfolder/file3.md"].sort()

    }

    def "everything in subfolder test"() {
        given:
        def filter = new GlobFilterPlugin("subfolder/**")

        def resourceDirectory = Paths.get("src/test/resources", "globTest")
        def files = getDataFromDirectory(resourceDirectory)

        when:
        def files2 = files.findAll { filter.filter(it) }

        then:
        files2.collect {it.path}.sort() == ["subfolder/file2.md", "subfolder/file3.md", "subfolder/subsubfolder/file4.png"].sort()


    }

}
