package com.networkedassets.git4c.selenium.singlefile

import com.networkedassets.git4c.selenium.RepoType
import com.networkedassets.git4c.selenium.SharedUtils
import com.networkedassets.git4c.selenium.utils.WebdriverExtensions.findElementById
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.Select
import uy.klutter.core.common.with
import java.util.concurrent.TimeUnit

object Utils: SharedUtils {

    fun WebDriver.createSingleFileMacro(
            repoType: RepoType,
            file: String? = null,
            toc: Boolean? = null,
            fileRange: Pair<Int, Int>? = null
    ) {

        val driver = this

        println(driver)

        val wait = FluentWait<WebDriver>(this)
                .withTimeout(15, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(org.openqa.selenium.NoSuchElementException::class.java)

        val tryClicking = FluentWait<WebDriver>(this)
                .withTimeout(15, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(WebDriverException::class.java)

        wait.with {

            switchTo().frame("wysiwygTextarea_ifr");

            wait.until { this@createSingleFileMacro.findElementById("tinymce").isEnabled }

            tryClicking.until { this@createSingleFileMacro.findElementById("tinymce").click() }

            findElementById("tinymce").click()

            switchTo().parentFrame()

            findElementById("rte-button-insert").click()

            until { findElementById("rte-insert-macro") }.click()

            until { findElementById("macro-Git4C Single File") }.click()

            tryClicking.until { findElementById("git4c-single_file_dialog-add_repository-button").click() }

            (this@createSingleFileMacro as JavascriptExecutor).executeScript("""$("div.aui-message span.icon-close").click()""")

            setCustomRepository(repoType, "My repo")
            val dialog = findElementById("singlefiledoc_macroDialog")

            if (file != null) {

                until { dialog.findElement(By.className("git4c-file-select-container")).findElement(By.className("aui-button")).isEnabled }

                dialog.findElement(By.className("git4c-file-select-container")).findElement(By.className("aui-button")).click()

                val newDialog = { findElementById("singlefiledoc_filetree_macroDialog") }

                until { newDialog().findElement(By.linkText(file)).isDisplayed }
                newDialog().findElement(By.linkText(file)).click()

                newDialog().findElement(By.id("dialog-next-button")).click()

            }

            if (toc != null) {

                until { dialog.findElement(By.id("git4c_singlefiledialog_checkbox_toc")).isEnabled }

                val checkbox = dialog.findElement(By.id("git4c_singlefiledialog_checkbox_toc"))
                if (checkbox.isSelected && !toc || !checkbox.isSelected && toc) {
                    checkbox.click()
                }

            }

            if (fileRange != null) {

                Select(dialog.findElement(By.id("show_type_group")).findElement(By.tagName("select"))).selectByVisibleText("Lines Range")

                //https://stackoverflow.com/questions/37113525/how-to-trigger-js-native-even-addeventlistenerchange-function-by-jquery
                (driver as JavascriptExecutor).executeScript(
                        """
                            $("#git4c_single_file_dialog_start_line").val(${fileRange.first})
                            var evt = document.createEvent('HTMLEvents');
                            evt.initEvent('change', true, true);
                            $("#git4c_single_file_dialog_start_line")[0].dispatchEvent(evt)
                            """
                )

                (driver as JavascriptExecutor).executeScript(
                        """
                            $("#git4c_single_file_dialog_end_line").val(${fileRange.second})
                            var evt = document.createEvent('HTMLEvents');
                            evt.initEvent('change', true, true);
                            $("#git4c_single_file_dialog_end_line")[0].dispatchEvent(evt)
                            """
                )

            }

            until { dialog.findElement(By.id("dialog-save-button")).isEnabled }

            (this@createSingleFileMacro as JavascriptExecutor).executeScript("""$("#singlefiledoc_macroDialog #dialog-save-button").click()""")

            until { !findElementById("singlefiledoc_macroDialog").isDisplayed }

            Thread.sleep(2000)

        }

    }

}