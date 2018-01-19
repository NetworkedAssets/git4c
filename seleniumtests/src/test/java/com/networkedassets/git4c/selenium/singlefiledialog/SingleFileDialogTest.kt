package com.networkedassets.git4c.selenium.singlefiledialog

import com.networkedassets.git4c.selenium.BaseSeleniumTest
import com.networkedassets.git4c.selenium.SSHKey
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.Select
import uy.klutter.core.common.with
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SingleFileDialogTest: BaseSeleniumTest() {

    @Test
    fun `Option to enable table of content is shown when file is markdown`() {

        val tryClicking = FluentWait<WebDriver>(driver)
                .withTimeout(15, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(WebDriverException::class.java)

        driver.with {

            wait.with {

                switchTo().frame("wysiwygTextarea_ifr");

                wait.until { findElementById("tinymce").isEnabled }

                tryClicking.until { findElementById("tinymce").click() }

                findElementById("tinymce").click()

                switchTo().parentFrame()

                findElementById("rte-button-insert").click()

                until { findElementById("rte-insert-macro") }.click()

                until { findElementById("macro-Git4C Single File") }.click()

                tryClicking.until { findElementById("git4c-single_file_dialog-add_repository-button").click() }

                (driver as JavascriptExecutor).executeScript("""$("div.aui-message span.icon-close").click()""")

                val repoType = (SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))

                setCustomRepository(repoType, "My repo")

                val fileSelect = Select(findElementById("git4c_singlefiledialog_select_file"))

                until { fileSelect.options.size == 3 }

                //FIXME
                Thread.sleep(1000)

                assertTrue { findElementById("git4c_singlefiledialog_checkbox_toc").isDisplayed }

            }

        }

    }

    @Test
    fun `Option to enable table of content is not shown when file is code`() {

        val tryClicking = FluentWait<WebDriver>(driver)
                .withTimeout(15, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(WebDriverException::class.java)

        driver.with {

            wait.with {

                switchTo().frame("wysiwygTextarea_ifr");

                wait.until { findElementById("tinymce").isEnabled }

                tryClicking.until { findElementById("tinymce").click() }

                findElementById("tinymce").click()

                switchTo().parentFrame()

                findElementById("rte-button-insert").click()

                until { findElementById("rte-insert-macro") }.click()

                until { findElementById("macro-Git4C Single File") }.click()

                tryClicking.until { findElementById("git4c-single_file_dialog-add_repository-button").click() }

                (driver as JavascriptExecutor).executeScript("""$("div.aui-message span.icon-close").click()""")

                val repoType = (SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))

                setCustomRepository(repoType, "My repo")

                val fileSelect = Select(findElementById("git4c_singlefiledialog_select_file"))

                until { fileSelect.options.size == 3 }

                fileSelect.selectByVisibleText("z.java")

                //FIXME
                Thread.sleep(1000)

                assertFalse { findElementById("git4c_singlefiledialog_checkbox_toc").isDisplayed }

            }

        }

    }


    @Test
    fun `When option to enable toc is selected, toc is shown`() {

        val tryClicking = FluentWait<WebDriver>(driver)
                .withTimeout(15, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(WebDriverException::class.java)

        driver.with {

            wait.with {

                switchTo().frame("wysiwygTextarea_ifr");

                wait.until { findElementById("tinymce").isEnabled }

                tryClicking.until { findElementById("tinymce").click() }

                findElementById("tinymce").click()

                switchTo().parentFrame()

                findElementById("rte-button-insert").click()

                until { findElementById("rte-insert-macro") }.click()

                until { findElementById("macro-Git4C Single File") }.click()

                tryClicking.until { findElementById("git4c-single_file_dialog-add_repository-button").click() }

                (driver as JavascriptExecutor).executeScript("""$("div.aui-message span.icon-close").click()""")

                val repoType = (SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))

                setCustomRepository(repoType, "My repo")

                val fileSelect = Select(findElementById("git4c_singlefiledialog_select_file"))

                until { fileSelect.options.size == 3 }

                //FIXME
                Thread.sleep(1000)

                until { findElementById("git4c_singlefiledialog_checkbox_toc").isDisplayed }

                assertTrue { findElementById("git4c_singlefiledialog_markup_toc").isDisplayed }

            }

        }

    }

    @Test
    fun `When option to enable toc not selected, toc is hidden`() {

        val tryClicking = FluentWait<WebDriver>(driver)
                .withTimeout(15, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(WebDriverException::class.java)

        driver.with {

            wait.with {

                switchTo().frame("wysiwygTextarea_ifr");

                wait.until { findElementById("tinymce").isEnabled }

                tryClicking.until { findElementById("tinymce").click() }

                findElementById("tinymce").click()

                switchTo().parentFrame()

                findElementById("rte-button-insert").click()

                until { findElementById("rte-insert-macro") }.click()

                until { findElementById("macro-Git4C Single File") }.click()

                tryClicking.until { findElementById("git4c-single_file_dialog-add_repository-button").click() }

                (driver as JavascriptExecutor).executeScript("""$("div.aui-message span.icon-close").click()""")

                val repoType = (SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))

                setCustomRepository(repoType, "My repo")

                val fileSelect = Select(findElementById("git4c_singlefiledialog_select_file"))

                until { fileSelect.options.size == 3 }

                //FIXME
                Thread.sleep(1000)

                until { findElementById("git4c_singlefiledialog_checkbox_toc").isDisplayed }

                findElementById("git4c_singlefiledialog_checkbox_toc").click()

                assertFalse { findElementById("git4c_singlefiledialog_markup_toc").isDisplayed }

            }

        }

    }


    @Test
    fun `When option to enable toc is selected, toc is shown in tree dialog`() {

        val tryClicking = FluentWait<WebDriver>(driver)
                .withTimeout(15, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(WebDriverException::class.java)

        driver.with {

            wait.with {

                switchTo().frame("wysiwygTextarea_ifr");

                wait.until { findElementById("tinymce").isEnabled }

                tryClicking.until { findElementById("tinymce").click() }

                findElementById("tinymce").click()

                switchTo().parentFrame()

                findElementById("rte-button-insert").click()

                until { findElementById("rte-insert-macro") }.click()

                until { findElementById("macro-Git4C Single File") }.click()

                tryClicking.until { findElementById("git4c-single_file_dialog-add_repository-button").click() }

                (driver as JavascriptExecutor).executeScript("""$("div.aui-message span.icon-close").click()""")

                val repoType = (SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))

                setCustomRepository(repoType, "My repo")

                val fileSelect = Select(findElementById("git4c_singlefiledialog_select_file"))

                until { fileSelect.options.size == 3 }

                val treeButton = findElementsByClassName("aui-iconfont-nav-children-large")[0]

                until { treeButton.isEnabled }

                treeButton.click()

                findElementById("singlefiledoc_filetree_macroDialog").findElement(By.linkText("README.md")).click()

                until { findElementById("git4c-single-dialog-tree-code-holder").isDisplayed }

                assertTrue { findElementById("git4c-single-dialog-tree-toc").isDisplayed }

            }

        }

    }


    @Test
    fun `When option to enable toc is disabled, toc is not shown in tree dialog`() {

        val tryClicking = FluentWait<WebDriver>(driver)
                .withTimeout(15, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(WebDriverException::class.java)

        driver.with {

            wait.with {

                switchTo().frame("wysiwygTextarea_ifr");

                wait.until { findElementById("tinymce").isEnabled }

                tryClicking.until { findElementById("tinymce").click() }

                findElementById("tinymce").click()

                switchTo().parentFrame()

                findElementById("rte-button-insert").click()

                until { findElementById("rte-insert-macro") }.click()

                until { findElementById("macro-Git4C Single File") }.click()

                tryClicking.until { findElementById("git4c-single_file_dialog-add_repository-button").click() }

                (driver as JavascriptExecutor).executeScript("""$("div.aui-message span.icon-close").click()""")

                val repoType = (SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))

                setCustomRepository(repoType, "My repo")

                val fileSelect = Select(findElementById("git4c_singlefiledialog_select_file"))

                until { fileSelect.options.size == 3 }

                until { findElementById("git4c_singlefiledialog_checkbox_toc").isDisplayed }

                findElementById("git4c_singlefiledialog_checkbox_toc").click()

                val treeButton = findElementsByClassName("aui-iconfont-nav-children-large")[0]

                until { treeButton.isEnabled }

                treeButton.click()

                findElementById("singlefiledoc_filetree_macroDialog").findElement(By.linkText("README.md")).click()

                until { findElementById("git4c-single-dialog-tree-code-holder").isDisplayed }

                assertFalse { findElementById("git4c-single-dialog-tree-toc").isDisplayed }

            }

        }

    }


    @Test
    fun `When macro is created repository is added to most recently used`() {

        val tryClicking = FluentWait<WebDriver>(driver)
                .withTimeout(15, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(WebDriverException::class.java)

        driver.with {

            wait.with {

                switchTo().frame("wysiwygTextarea_ifr");

                wait.until { findElementById("tinymce").isEnabled }

                tryClicking.until { findElementById("tinymce").click() }

                findElementById("tinymce").click()

                switchTo().parentFrame()

                findElementById("rte-button-insert").click()

                until { findElementById("rte-insert-macro") }.click()

                until { findElementById("macro-Git4C Single File") }.click()

                tryClicking.until { findElementById("git4c-single_file_dialog-add_repository-button").click() }

                (driver as JavascriptExecutor).executeScript("""$("div.aui-message span.icon-close").click()""")

                val repoType = (SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))

                setCustomRepository(repoType, "My repo")


            }
            wait.until { driver.findElement(By.id("dialog-save-button")).isEnabled }
            tryClicking.until { driver.findElement(By.id("dialog-save-button")).click() }

            wait.until { !driver.findElementById("multifiledoc_macroDialog").isDisplayed }

            wait.with {
                switchTo().frame("wysiwygTextarea_ifr");
                tryClicking.until { driver.findElement(By.className("editor-inline-macro")).click() }
                switchTo().parentFrame()
                tryClicking.until { driver.findElement(By.id("property-panel")).findElements(By.tagName("a"))[0].click() }
                val label = until { driver.findElements(By.tagName("label")).find { it.text == "Repository" } }
                val repoSelect = label!!.findElement(By.xpath("..")).findElement(By.tagName("select"))
                assertTrue { repoSelect.findElements(By.tagName("option")).filter { it.text == "My repo" }.size > 1 }
            }

        }
    }


}