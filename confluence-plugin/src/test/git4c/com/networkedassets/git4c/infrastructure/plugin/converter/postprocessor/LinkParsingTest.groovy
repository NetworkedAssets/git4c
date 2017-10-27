package com.networkedassets.git4c.infrastructure.plugin.converter.postprocessor

import com.networkedassets.git4c.infrastructure.UuidIdentifierGenerator
import com.networkedassets.git4c.infrastructure.plugin.converter.main.JSoupPostProcessor
import spock.lang.Specification

class LinkParsingTest extends Specification {

    def "Inline text url should be changed to link html tag"() {

        given:
        def converter = new JSoupPostProcessor(new UuidIdentifierGenerator())
        def source = """<span>Linki do stron: <b><span>Text before</span>Google: https://google.pl Yahoo: yahoo.com <span>SPAN</span></b> Text po</span>"""

        when:
        def html = converter.parseLinks(source)

        then:
        html == """<span>Linki do stron: <b><span>Text before</span>Google: <a href="https://google.pl">https://google.pl</a> Yahoo: <a href="yahoo.com">yahoo.com</a> <span>SPAN</span></b> Text po</span>"""


    }

    def "a links shouldn't be changed"() {

        given:
        def converter = new JSoupPostProcessor(new UuidIdentifierGenerator())
        def source = """<a href="reddit.com">https://google.com</a>"""

        when:
        def html = converter.parseLinks(source)

        then:
        html == source

    }


    def "http links should work properly"(String url) {

        given:
        def converter = new JSoupPostProcessor(new UuidIdentifierGenerator())
        def source = """<p>$url</p>"""

        when:
        def html = converter.parseLinks(source)

        then:
        html == """<p><a href="$url">$url</a></p>"""

        where:
        url                                        | _
        "http://google.com"                        | _
        "http://{{variable}}.google.com"           | _
        "http://pc-aressel:1990/confluence"        | _
        "https://{hostname}/screens/frameset.html" | _
        "https://{hostname}:8443/wsg/"             | _
        "https://{hostname}:7443/api/Carrier.html" | _
    }

}
