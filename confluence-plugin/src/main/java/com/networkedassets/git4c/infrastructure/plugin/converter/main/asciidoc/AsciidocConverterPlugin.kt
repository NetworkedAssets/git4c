package com.networkedassets.git4c.infrastructure.plugin.converter.main.asciidoc

import com.networkedassets.git4c.core.business.FileIgnorer
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.infrastructure.plugin.converter.main.MainConverterPlugin
import org.apache.commons.io.FilenameUtils
import org.asciidoctor.Asciidoctor
import org.asciidoctor.OptionsBuilder
import org.asciidoctor.SafeMode
import org.asciidoctor.internal.JRubyRuntimeContext
import org.jruby.Ruby
import org.jruby.RubyInstanceConfig
import org.jruby.RubyString
import org.jruby.RubyStruct
import org.jruby.javasupport.JavaEmbedUtils.rubyToJava
import org.jruby.runtime.builtin.IRubyObject
import java.nio.file.Paths

class AsciidocConverterPlugin private constructor(
        val confluence: Boolean
) : MainConverterPlugin, FileIgnorer {
    companion object {

        var converterPlugin: AsciidocConverterPlugin? = null
        val lock = java.lang.Object()

        @JvmStatic
        fun get(confluence: Boolean): AsciidocConverterPlugin {

            if (converterPlugin == null) {
                synchronized(lock) {
                    if (converterPlugin == null) {
                        converterPlugin = AsciidocConverterPlugin(confluence)
                    }
                }
            }

            if (confluence != converterPlugin!!.confluence) {
                throw RuntimeException("Plugin was already created with different options")
            }

            return converterPlugin!!
        }
    }

    private val asciidoctor: Asciidoctor

    private val ruby: Ruby

    private val lock = java.lang.Object()

    private val jailHolder: JailHolder
    private val arrayHolder: ArrayHolder

    init {

        val config = RubyInstanceConfig()
        config.loader = this::class.java.classLoader

        asciidoctor = Git4CAsciidocFactory.create(confluence)

        ruby = JRubyRuntimeContext.get()

        ruby.evalScriptlet("require 'git4c/jruby/asciidoctor/asciidoctor/path_resolver_git4c.rb'")
        ruby.evalScriptlet("require 'git4c/jruby/asciidoctor/asciidoctor/path_resolver_git4c_include.rb'")

        val jailHolderRb = ruby.evalScriptlet("Asciidoctor::JailHolder.new()")
        val arrayHolderRb = ruby.evalScriptlet("Asciidoctor::ArrayHolder.new()")

        jailHolder = rubyToJava(ruby, jailHolderRb)
        arrayHolder = rubyToJava(ruby, arrayHolderRb)

    }

    override fun convert(fileData: ImportedFileData): String = synchronized(lock) {
        val options = OptionsBuilder.options()
                .toFile(false)
                .safe(SafeMode.SAFE)

        jailHolder.set_jail_location(fileData.context.toFile().absolutePath)

        val fileToConvert = fileData.context.resolve(fileData.path).toFile()

        val file = asciidoctor.convertFile(fileToConvert, options)

        jailHolder.clear_jail_location()

        file

    }

    override fun getFilesToIgnore(fileData: ImportedFileData): List<String> {
        return synchronized(lock) {

            val context = fileData.context

            jailHolder.set_jail_location(fileData.context.toFile().absolutePath)

            val options = OptionsBuilder.options()
                    .toFile(false)
                    .safe(SafeMode.SAFE)

            val fileToConvert = fileData.context.resolve(fileData.path).toFile()
            asciidoctor.convertFile(fileToConvert, options)

            val files = arrayHolder._array
                    .filterIsInstance(RubyStruct::class.java)
                    .map {
                        val dir = (it[0] as RubyString).decodeString()
                        val file = (it[1] as RubyString).decodeString()
                        val filePath = Paths.get(dir, file).normalize()
                        context.toAbsolutePath().relativize(filePath).toString()
                    }

            jailHolder.clear_jail_location()
            arrayHolder.clear_array()

            files
                    .map { FilenameUtils.separatorsToUnix(it) }
                    .distinct()

        }
    }

    private inline fun <reified T> rubyToJava(ruby: Ruby, objRb: IRubyObject): T = rubyToJava(ruby, objRb, T::class.java) as T


    override fun supportedExtensions() = listOf("adoc", "ad", "asciidoc", "asc")
}