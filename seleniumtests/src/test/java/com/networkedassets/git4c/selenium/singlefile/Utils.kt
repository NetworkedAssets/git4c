package com.networkedassets.git4c.selenium.singlefile

import com.networkedassets.git4c.selenium.RepoType
import com.networkedassets.git4c.selenium.SharedUtils
import com.networkedassets.git4c.selenium.utils.WebdriverExtensions.findElementById
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.support.ui.FluentWait
import uy.klutter.core.common.with
import java.util.concurrent.TimeUnit

object Utils: SharedUtils {

    fun WebDriver.createSingleFileMacro(repoType: RepoType) {

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

            until { dialog.findElement(By.id("dialog-close-button")).isEnabled }

            (this@createSingleFileMacro as JavascriptExecutor).executeScript("""$("#singlefiledoc_macroDialog #dialog-close-button").click()""")

            until { !findElementById("singlefiledoc_macroDialog").isDisplayed }

            Thread.sleep(2000)

        }

    }

}