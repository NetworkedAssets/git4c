package com.networkedassets.git4c.selenium.multifile

import com.networkedassets.git4c.selenium.BaseSeleniumTest
import com.networkedassets.git4c.selenium.NoAuth
import com.networkedassets.git4c.selenium.SSHKey
import com.networkedassets.git4c.selenium.multifile.Utils.createMultifileMacro
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import uy.klutter.core.common.with
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MultiFileUsageTest : BaseSeleniumTest() {

    @Test
    fun `Tree in dialog test`() {

        driver.createPageInside {
            createMultifileMacro(SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))
        }

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
    fun `After clicking on file in filetree content should change`() {
        driver.with {
            wait.with {

                createPageInside {
                    createMultifileMacro(SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))
                }

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
    fun `Clicking on show source button should open dialog with source`() {
        driver.with {
            wait.with {

                createPageInside {
                    createMultifileMacro(SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))
                }

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
    fun `Clicking on View Commits button should open dialog with commits`() {

        driver.with {
            wait.with {

                createPageInside {
                    createMultifileMacro(SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))
                }

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

    @Test
    fun `After clicking on file in filetree and navigating back file should be the same`() {
        driver.with {
            wait.with {

                createPageInside {
                    createMultifileMacro(SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))
                }

                until { findElementById("git4c-main-content").isDisplayed }

                until { findElementByClassName("git4c-file").text.isNotEmpty() }

                val fileNameToolbar = findElementByClassName("git4c-file").text

                assertEquals("README.md", fileNameToolbar)

                until { findElementsByTagName("label").find { it.text == "README2.md" } }!!.click()

                assertTrue(driver.currentUrl.contains("README2.md"))

                until { findElementByClassName("git4c-file").text == "README2.md" }

                assertEquals("README2.md", findElementByClassName("git4c-file").text)

                assertEquals("Second file", findElementById("git4c-content").text.trim())

                driver.navigate().back()

                until { findElementByClassName("git4c-file").text == "README.md"  }

                val label = until { findElementsByTagName("label").find { it.text == "README.md" } }

                val isActive = label!!.findElement(By.xpath("..")).findElement(By.xpath("..")).getAttribute("class").contains("active")

                assertTrue { isActive }

            }
        }
    }


    @Test
    fun `Multi file repository with NoAuth repository should be created with Root directory selected`() {
        driver.with {
            wait.with {
                createPageInside {
                    createMultifileMacro(NoAuth("https://github.com/jaagr/polybar"), true)
                }
                until { findElementById("git4c-main-content").isDisplayed }
                until { findElementByClassName("git4c-file").text.isNotEmpty() }

                val firstDirectory = driver.findElement(By.id("aui-vnav")).findElements(By.className("node"))[0].text
                assertTrue(firstDirectory == ".hooks")
            }
        }
    }



    @Test
    fun `Multi file macro will add anchor to query params in url when anchor used`() {
        driver.with {
            wait.with {
                createPageInside {
                    createMultifileMacro(NoAuth("https://github.com/jaagr/polybar"))
                }
                until { findElementById("git4c-main-content").isDisplayed }
                until { findElementByClassName("git4c-file").text.isNotEmpty() }
                val pageurl = until { currentUrl }
                get( pageurl.substringBeforeLast("/") + "/README.md?branch=master")
                until { findElement(By.className("git4c-toc")).findElements(By.tagName("a")).filter{it.text == "Polybar"}[0]}.click()

                assertTrue { !pageurl.contains("anchor") }
                assertTrue { currentUrl.contains("anchor") }

            }
        }
    }


    @Test
    fun `Multi file macro will navigate to anchor in url`() {
        driver.with {
            wait.with {
                createPageInside {
                    createMultifileMacro(NoAuth("https://github.com/jaagr/polybar"))
                }
                until { findElementById("git4c-main-content").isDisplayed }
                until { findElementByClassName("git4c-file").text.isNotEmpty() }
                val pageurl = currentUrl
                get( pageurl.substringBeforeLast("/") + "/README.md?branch=master")
                until { findElement(By.className("git4c-toc")).findElements(By.tagName("a")).filter{it.text == "Polybar"}[0]}.click()


                val newPageUrl = currentUrl

                get(pageurl)
                get(newPageUrl)

                assertTrue { (driver as JavascriptExecutor).executeScript("return window.pageYOffset;") != 0 }

            }
        }
    }

}