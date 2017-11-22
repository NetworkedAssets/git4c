package com.networkedassets.git4c.selenium.publicaccess

import com.networkedassets.git4c.selenium.BaseSeleniumTest
import com.networkedassets.git4c.selenium.SSHKey
import com.networkedassets.git4c.selenium.annotations.Public
import com.networkedassets.git4c.selenium.multifile.Utils.createMultifileMacro
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import uy.klutter.core.common.with
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertTrue

//Copied MultiFileUsageTest with @Public annotations and cleaning cookies


class PublicAccessMultiFileUsageTest : BaseSeleniumTest() {

    @Test
    @Public
    fun `Tree in dialog test`() {

        driver.createPageInside {
            createMultifileMacro(SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))
        }
        wait.until { driver.findElementById("git4c-filetree-collapse_button").isDisplayed }

        driver.manage().deleteAllCookies()
        driver.navigate().refresh()

        wait.until { driver.findElementById("login-link").isDisplayed }

        assertEquals("Log in", driver.findElementById("login-link").text)

        wait.until { driver.findElementById("git4c-filetree-collapse_button").isDisplayed }

        (driver as JavascriptExecutor).executeScript("""$("#git4c-filetree-collapse_button").click()""")

        wait.until { driver.findElementById("git4c-toolbar_filetree-button").isDisplayed }

        (driver as JavascriptExecutor).executeScript("""$("#git4c-toolbar_filetree-button").click()""")

        wait.until {
            val dialog = driver.findElementById("git4c-tree-dialog")
            dialog.getCssValue("display") != "none"
        }

    }


    @Test
    @Public
    fun `After clicking on file in filetree content should change`() {
        driver.with {
            wait.with {

                createPageInside {
                    createMultifileMacro(SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))
                }


                until { findElementById("git4c-main-content").isDisplayed }

                driver.manage().deleteAllCookies()
                driver.navigate().refresh()

                wait.until { driver.findElementById("login-link").isDisplayed }

                assertEquals("Log in", driver.findElementById("login-link").text)

                until { findElementById("git4c-main-content").isDisplayed }

                until { findElementByClassName("git4c-file").text.isNotEmpty() }

                val fileNameToolbar = findElementByClassName("git4c-file").text

                assertEquals("README.md", fileNameToolbar)

                until { findElementsByTagName("label").find { it.text == "README2.md" } }!!.click()

                assertTrue(driver.currentUrl.contains("README2.md"))

                until { findElementByClassName("git4c-file").text == "README2.md" }

                assertEquals("README2.md", findElementByClassName("git4c-file").text)

                assertEquals("Second file", findElementById("git4c-content").text.trim())

            }
        }
    }

    @Test
    @Public
    fun `Clicking on show source button should open dialog with source`() {
        driver.with {
            wait.with {

                createPageInside {
                    createMultifileMacro(SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))
                }

                until { findElementById("git4c-main-content").isDisplayed }

                driver.manage().deleteAllCookies()
                driver.navigate().refresh()

                wait.until { driver.findElementById("login-link").isDisplayed }

                assertEquals("Log in", driver.findElementById("login-link").text)

                until { findElementById("git4c-main-content").isDisplayed }

                val source = """
# EmptyNARepo

Nothing interesting to see here
""".trim()

                //FIXME
                until { findElementByClassName("aui-iconfont-devtools-file").isDisplayed }

                findElementByClassName("aui-iconfont-devtools-file").click()

                val displayedSource = until { findElementById("git4c-dialog-code") }.text.trim()

                assertEquals(source, displayedSource)

            }
        }
    }

    @Test
    @Public
    fun `Clicking on View Commits button should open dialog with commits`() {

        driver.with {
            wait.with {

                createPageInside {
                    createMultifileMacro(SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))
                }

                until { findElementById("git4c-main-content").isDisplayed }

                driver.manage().deleteAllCookies()
                driver.navigate().refresh()

                wait.until { driver.findElementById("login-link").isDisplayed }

                assertEquals("Log in", driver.findElementById("login-link").text)

                until { findElementById("git4c-main-content").isDisplayed }

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

}