package com.networkedassets.git4c.selenium.repository

import com.networkedassets.git4c.selenium.BaseSeleniumTest
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.Select
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreateRepositoryTests: BaseSeleniumTest() {

    @Test
    fun `When SSH key in wrong format is given, error is shown`() {

        driver.get(adminPanelLocation)

        wait.until { driver.findElementById("add_repository-button") }.click()

        wait.until { driver.findElementById("doc_macro-repo_name") }.sendKeys("My repository")

        driver.findElementById("doc_macro-repo_url").sendKeys("git@github.com:jereksel/EmptyNARepo.git")

        Select(driver.findElementById("doc_macro-auth_type")).selectByVisibleText("SSH: Private Key")

        wait.until { driver.findElementById("doc_macro-sshkey").isDisplayed }
        driver.findElementById("doc_macro-sshkey").sendKeys("THIS IS NOT SSH KEY")

        wait.until { driver.findElementById("custom_repository_dialog-close-button").isEnabled }
        driver.findElementById("custom_repository_dialog-close-button").click()

        wait.until {
            it.findElementById("custom_repository-dialog").findElements(By.className("aui-message-error")).find { it.isDisplayed } != null
        }

        val expected = """
Error!
Could not parse the provided SSH key. It may be in wrong format. The key should look like:
-----BEGIN RSA PRIVATE KEY-----
....
-----END RSA PRIVATE KEY-----
"""

        assertEquals(expected.trim(), driver.findElementById("custom_repository-dialog").findElements(By.className("aui-message-error")).find { it.isDisplayed }?.text?.trim())

    }

}