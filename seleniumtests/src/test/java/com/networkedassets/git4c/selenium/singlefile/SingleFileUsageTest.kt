package com.networkedassets.git4c.selenium.singlefile

import com.networkedassets.git4c.selenium.BaseSeleniumTest
import com.networkedassets.git4c.selenium.NoAuth
import com.networkedassets.git4c.selenium.SSHKey
import com.networkedassets.git4c.selenium.singlefile.Utils.createSingleFileMacro
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.FluentWait
import uy.klutter.core.common.with
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SingleFileUsageTest : BaseSeleniumTest() {

    @Test
    fun `Single file line number test`() {

        driver.with {
            wait.with {

                createPageInside {
                    createSingleFileMacro(NoAuth("https://github.com/jaagr/polybar"))
                }

                until { findElementByClassName("git4c-singlefile-app") }

            }
        }

        wait.until { driver.findElementByClassName("git4c-singlefile-app") }

        wait.until { driver.findElementById("git4c-line_numbers-toggle_button").isDisplayed }

        wait.until { driver.findElementByClassName("line-numbers-rows").isDisplayed }

        (driver as JavascriptExecutor).executeScript("""$("#git4c-line_numbers-toggle_button").click()""")

        wait.until { !driver.findElementByClassName("line-numbers-rows").isDisplayed }

        (driver as JavascriptExecutor).executeScript("""$("#git4c-line_numbers-toggle_button").click()""")

        wait.until { driver.findElementByClassName("line-numbers-rows").isDisplayed }

    }


    @Test
    fun collapseTest() {

        driver.with {
            wait.with {

                createPageInside {
                    createSingleFileMacro(NoAuth("https://github.com/jaagr/polybar"))
                }

                until { findElementByClassName("git4c-singlefile-app") }

            }
        }

        wait.until { driver.findElementByClassName("git4c-collapse-source-button") }.click()

        assertEquals("Expand source", driver.findElementByClassName("git4c-collapse-source-button").text.trim())

        assertFalse(driver.findElementByClassName("git4c-singlefile-content").isDisplayed)

        driver.findElementByClassName("git4c-collapse-source-button").click()

        assertTrue(driver.findElementByClassName("git4c-singlefile-content").isDisplayed)

    }

    @Test
    fun `Clicking on View Commits button should open dialog with commits`() {

        driver.with {
            wait.with {

                createPageInside {
                    createSingleFileMacro(NoAuth("https://github.com/jaagr/polybar"))
                }

                until { findElementByClassName("git4c-singlefile-app") }

            }
        }

        val dialogId = "git4c_commit_history_dialog"

        wait.until { driver.findElementByLinkText("View Commits").isDisplayed }
        driver.findElementByLinkText("View Commits").click()

        wait.until {
            val dialog = driver.findElementById(dialogId)
            dialog.getCssValue("display") != "none"
        }

        wait.until {
            val dialog = driver.findElementById(dialogId)
            val content = dialog.findElement(By.className("aui-dialog2-content"))
            content.findElements(By.tagName("div")).size > 0
        }

        driver.findElementById(dialogId).findElement(By.className("aui-iconfont-close-dialog")).click()

        wait.until {
            val dialog = driver.findElementById(dialogId)
            dialog.getCssValue("display") == "none"
        }


    }

    @Test
    fun `Clicking on show source button should open dialog with source`() {
        driver.with {
            val driver = this
            wait.with {

                val longwait = FluentWait<WebDriver>(driver)
                                .withTimeout(1, TimeUnit.MINUTES)
                                .pollingEvery(1, TimeUnit.SECONDS)
                                .ignoring(org.openqa.selenium.NoSuchElementException::class.java, StaleElementReferenceException::class.java)


                createPageInside {
                    createSingleFileMacro(SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))
                }

                until { findElementByClassName("git4c-singlefile-app") }


                val source = """
# EmptyNARepo

Nothing interesting to see here
""".trim()

                //FIXME
                longwait.until { findElementByClassName("aui-iconfont-devtools-file").isDisplayed }

                findElementByClassName("aui-iconfont-devtools-file").click()

                val displayedSource = until { findElementById("git4c-dialog-code") }.text.trim()

                assertEquals(source, displayedSource)

            }
        }
    }

}