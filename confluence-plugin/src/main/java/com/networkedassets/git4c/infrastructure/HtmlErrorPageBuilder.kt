package com.networkedassets.git4c.infrastructure

import com.networkedassets.git4c.core.business.ErrorPageBuilder
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import org.apache.commons.lang3.exception.ExceptionUtils

class HtmlErrorPageBuilder: ErrorPageBuilder {

    override fun build(file: ImportedFileData, ex: Exception): String {

        val stackTrace = ExceptionUtils.getStackTrace(ex)

        val page = """
                    <div class="aui-message aui-message-error">
                        <p class="title">
                            <strong>Error!</strong>
                        </p>
                        <p>Error while reading file. Please send the following log to the plugin developers.</p>
                    </div>

                    <pre>
                        <code class="git4c-code" style="overflow-x: auto;">
                        File: ${file.path}
                        Message: ${ex.message}

                        Stacktrace:
                        $stackTrace

                        </code>
                    </pre>
                    """.lines().joinToString(separator = "\n") { it.trim() }

        return page
    }
}