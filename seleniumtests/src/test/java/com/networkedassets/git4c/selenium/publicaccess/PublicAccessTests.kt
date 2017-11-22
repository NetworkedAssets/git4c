package com.networkedassets.git4c.selenium.publicaccess

import com.networkedassets.git4c.selenium.BaseSeleniumTest
import com.networkedassets.git4c.selenium.SSHKey
import com.networkedassets.git4c.selenium.annotations.Public
import com.networkedassets.git4c.selenium.multifile.Utils.createMultifileMacro
import com.networkedassets.git4c.selenium.singlefile.Utils.createSingleFileMacro
import org.junit.Test
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PublicAccessTests: BaseSeleniumTest() {

    @Test
    @Public
    fun `Multifile macro is available for anonymous users`() {

        driver.createPageInside {
            createMultifileMacro(SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))
        }

        wait.until { driver.findElementById("git4c-filetree-collapse_button").isDisplayed }

        driver.manage().deleteAllCookies()
        driver.navigate().refresh()

        wait.until { driver.findElementById("login-link").isDisplayed }

        assertEquals("Log in", driver.findElementById("login-link").text)

        wait.until { driver.findElementById("git4c-main-content") }

        wait.until { driver.findElementById("git4c-breadcrumbs-div").isDisplayed }

    }

    @Test
    @Public
    fun `Single file macro is available for anonymous users`() {

        driver.createPageInside {
            createSingleFileMacro(SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))
        }
        wait.until { driver.findElementByClassName("git4c-singlefile-app") }

        driver.manage().deleteAllCookies()
        driver.navigate().refresh()

        wait.until { driver.findElementById("login-link").isDisplayed }

        assertEquals("Log in", driver.findElementById("login-link").text)

        wait.until { driver.findElementByClassName("git4c-singlefile-app") }

        wait.until { driver.findElementByClassName("git4c-singlefile-content-markdown") }

        val text = driver.findElementByClassName("git4c-singlefile-content-markdown").text

        assertTrue(text.contains("Nothing interesting to see here"))

    }

}