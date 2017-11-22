package com.networkedassets.git4c.selenium.multifile

import com.networkedassets.git4c.selenium.RepoType
import com.networkedassets.git4c.selenium.SharedUtils
import com.networkedassets.git4c.selenium.utils.WebdriverExtensions.findElementById
import org.openqa.selenium.ElementNotVisibleException
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.support.ui.FluentWait
import java.util.concurrent.TimeUnit

object Utils: SharedUtils {

    fun WebDriver.createMultifileMacro(repoType: RepoType, selectRootDirectory: Boolean = false) {

        val driver = this

        val wait = FluentWait<WebDriver>(driver)
                .withTimeout(15, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoreAll(listOf(org.openqa.selenium.NoSuchElementException::class.java, ElementNotVisibleException::class.java, WebDriverException::class.java))

        val tryClicking = FluentWait<WebDriver>(driver)
                .withTimeout(15, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(WebDriverException::class.java)


        driver.switchTo().frame("wysiwygTextarea_ifr");

        wait.until { driver.findElementById("tinymce").isEnabled }

        tryClicking.until { driver.findElementById("tinymce").click() }

//        Thread.sleep(10000)

//        driver.findElementById("tinymce").click()

        driver.switchTo().parentFrame()

        driver.findElementById("rte-button-insert").click()

        wait.until { driver.findElementById("rte-insert-macro") }.click()

        wait.until { driver.findElementById("macro-Git4C") }.click()

        (driver as JavascriptExecutor).executeScript("""$("div.aui-message span.icon-close").click()""")

        tryClicking.until { driver.findElementById("git4c-multi_file_dialog-add_repository-button").click() }

        setCustomRepository(repoType, "Myrepo")

        if(selectRootDirectory) {
            tryClicking.until { driver.findElementById("git4c-multi_file_dialog-select_root_dir_button").click() }

            setRootDir()
        }

//        wait.until(ExpectedConditions.elementToBeClickable(driver.findElementById("dialog-close-button"))).click()

        tryClicking.until { driver.findElementById("dialog-close-button").click() }

        wait.until { !driver.findElementById("multifiledoc_macroDialog").isDisplayed }

        Thread.sleep(2000)

    }
}