package com.networkedassets.git4c.selenium.multifile

import com.networkedassets.git4c.selenium.BaseSeleniumTest
import com.networkedassets.git4c.selenium.NoAuth
import com.networkedassets.git4c.selenium.SSHKey
import com.networkedassets.git4c.selenium.multifile.Utils.createMultifileMacro
import org.junit.Test
import java.nio.file.Paths

class MultiFileCreationTest : BaseSeleniumTest() {

    @Test
    fun `Multi file repository with NoAuth repository should be created`() {

        driver.createPageInside {
            createMultifileMacro(NoAuth("https://github.com/jaagr/polybar"))
        }

        wait.until { driver.findElementById("git4c-main-content") }

    }

    @Test
    fun `Multi file repository with SSHAuth should be created`() {

        driver.createPageInside {
            createMultifileMacro(SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))
        }

        wait.until { driver.findElementById("git4c-main-content") }

    }
}