package com.networkedassets.git4c.infrastructure.plugin.converter.main.markdown

import com.networkedassets.git4c.core.bussiness.ConverterPlugin

interface InternalConverterPlugin : ConverterPlugin {
    fun supportedExtensions(): List<String>
}