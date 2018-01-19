package com.networkedassets.git4c.selenium.codeeditor

import com.networkedassets.git4c.selenium.BaseSeleniumTest
import com.networkedassets.git4c.selenium.NoAuth
import com.networkedassets.git4c.selenium.multifile.Utils.createMultifileMacro
import com.networkedassets.git4c.selenium.singlefile.Utils.createSingleFileMacro
import org.junit.Ignore
import org.junit.Test
import org.openqa.selenium.NoAlertPresentException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.FluentWait
import uy.klutter.core.common.with
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

class CodeEditorTest : BaseSeleniumTest() {

    @Test
    fun `Code editor in single file macro`() {

        val waitForAlert = FluentWait<WebDriver>(driver)
                .withTimeout(30, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(NoAlertPresentException::class.java)

        driver.with {
            wait.with {

                createPageInside {
                    createSingleFileMacro(NoAuth("https://github.com/github/gitignore.git"))
                }

                until { findElementByClassName("git4c-singlefile-app") }

            }
        }

        wait.until { driver.findElementByClassName("git4c-singlefile-app") }

        wait.until { driver.findElementById("git4c-toolbar-edit-button").isDisplayed }

        driver.findElementById("git4c-toolbar-edit-button").click()

        wait.until { driver.findElementById("git4c-edit-dialog-publish-button").isDisplayed }

        driver.findElementById("git4c_commit_message").sendKeys("My commit message")

        driver.findElementById("git4c-edit-dialog-publish-button").click()

//        val alert = waitForAlert.until { driver.switchTo().alert() }
//
//        assertEquals("File upload failed", alert.text)
//
//        alert.accept()

    }


    @Test
    fun `Code editor in multi file macro`() {

        val longWait = FluentWait<WebDriver>(driver)
                .withTimeout(20, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(NoAlertPresentException::class.java)

        driver.with {
            wait.with {

                createPageInside {
                    createMultifileMacro(NoAuth("https://github.com/github/gitignore.git"))
                }

                until { findElementById("git4c-main-content").isDisplayed }

            }
        }

        longWait.until { driver.findElementById("git4c-toolbar-edit-button").isDisplayed }

        driver.findElementById("git4c-toolbar-edit-button").click()

        wait.until { driver.findElementById("git4c-edit-dialog-publish-button").isDisplayed }

        driver.findElementById("git4c_commit_message").sendKeys("My commit message")

        driver.findElementById("git4c-edit-dialog-publish-button").click()

//        val alert = waitForAlert.until { driver.switchTo().alert() }
//
//        assertEquals("File upload failed", alert.text)
//
//        alert.accept()

    }


    @Test
    fun `Error is shown when commit message is empty`() {

        driver.with {
            wait.with {

                createPageInside {
                    createMultifileMacro(NoAuth("https://github.com/github/gitignore.git"))
                }

                until { findElementById("git4c-main-content").isDisplayed }

            }
        }

        wait.until { driver.findElementById("git4c-toolbar-edit-button").isDisplayed }

        driver.findElementById("git4c-toolbar-edit-button").click()

        wait.until { driver.findElementById("git4c-edit-dialog-publish-button").isDisplayed }

        driver.findElementById("git4c-edit-dialog-publish-button").click()

        val error = driver.findElementById("git4c-edit-file-dialog-error-message").text.trim()

        assertEquals("You have to write what did you change!", error)

    }

    @Test
    @Ignore("Branch option is hidden")
    fun `Error is shown when branch is selected and empty`() {

        driver.with {
            wait.with {

                createPageInside {
                    createMultifileMacro(NoAuth("https://github.com/github/gitignore.git"))
                }

                until { findElementById("git4c-main-content").isDisplayed }

            }
        }

        wait.until { driver.findElementById("git4c-toolbar-edit-button").isDisplayed }

        driver.findElementById("git4c-toolbar-edit-button").click()

        wait.until { driver.findElementById("git4c-edit-dialog-publish-button").isDisplayed }

        driver.findElementById("git4c_custom_branch_checkbox").click()

//        driver.findElementById("git4c_custom_branch_input").sendKeys("")

        driver.findElementById("git4c-edit-dialog-publish-button").click()

        val error = driver.findElementById("git4c-edit-file-dialog-error-message").text.trim()

        assertEquals("You have to write what did you change!", error)

    }



}