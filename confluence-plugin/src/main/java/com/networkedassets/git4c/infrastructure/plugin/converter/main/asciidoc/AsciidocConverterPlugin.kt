package com.networkedassets.git4c.infrastructure.plugin.converter.main.asciidoc

import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.infrastructure.plugin.converter.main.MainConverterPlugin
import org.asciidoctor.Asciidoctor
import org.jruby.RubyInstanceConfig
import org.jruby.javasupport.JavaEmbedUtils
import java.util.*

class AsciidocConverterPlugin(
        val confluence: Boolean = true
) : MainConverterPlugin {

    private val asciidoctor: Asciidoctor

    init {

        if (confluence) {
            val asciidoctorVersion = "1.5.6.1"

            //Hacks to get Asciidoctor working with Confluence's OSGi
            val config = RubyInstanceConfig()
            config.loader = this::class.java.classLoader
            val r = JavaEmbedUtils.initialize(Arrays.asList("META-INF/jruby.home/lib/ruby/stdlib", "gems/asciidoctor-$asciidoctorVersion/lib", "gems/asciidoctor-$asciidoctorVersion/", "gems"), config)
            r.evalScriptlet("require 'gems/asciidoctor-$asciidoctorVersion/lib/asciidoctor.rb'")
        }

        asciidoctor = Asciidoctor.Factory.create(this::class.java.classLoader)

    }

    override fun convert(fileData: ImportedFileData): String {
        return asciidoctor.convert(String(fileData.content()), mapOf())
    }


    override fun supportedExtensions() = listOf("adoc", "ad", "asciidoc", "asc")
}