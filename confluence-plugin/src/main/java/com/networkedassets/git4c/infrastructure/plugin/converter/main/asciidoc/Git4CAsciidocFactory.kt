package com.networkedassets.git4c.infrastructure.plugin.converter.main.asciidoc

import org.asciidoctor.Asciidoctor
import org.asciidoctor.internal.AdocFunction
import org.asciidoctor.internal.Git4CJRubyAsciidoctor
import org.jruby.Ruby
import org.jruby.RubyInstanceConfig

object Git4CAsciidocFactory {

    fun create(confluence: Boolean): Asciidoctor {

        val asciidoctorVersion = "1.5.6.1"

        val config = RubyInstanceConfig()
        config.loader = this::class.java.classLoader

        val loadPath = if(confluence) {
            listOf("META-INF/jruby.home/lib/ruby/stdlib/", "gems/asciidoctor-$asciidoctorVersion/lib/", "gems/asciidoctor-$asciidoctorVersion/", "gems/", "git4c/")
        } else {
            listOf()
        }

        val init: AdocFunction<Ruby> = if (confluence) {
            AdocFunction {
                it.evalScriptlet("require 'gems/asciidoctor-$asciidoctorVersion/lib/asciidoctor.rb'")
            }
        } else {
            AdocFunction {}
        }

        return Git4CJRubyAsciidoctor.create(loadPath, this::class.java.classLoader, init)

    }

}