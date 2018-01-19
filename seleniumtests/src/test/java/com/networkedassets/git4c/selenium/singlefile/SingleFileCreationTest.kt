package com.networkedassets.git4c.selenium.singlefile

import com.networkedassets.git4c.selenium.BaseSeleniumTest
import com.networkedassets.git4c.selenium.NoAuth
import com.networkedassets.git4c.selenium.SSHKey
import com.networkedassets.git4c.selenium.singlefile.Utils.createSingleFileMacro
import org.junit.Test
import java.nio.file.Paths

class SingleFileCreationTest : BaseSeleniumTest() {

    @Test
    fun `Single file repository with NoAuth repository should be created`() {

        driver.createPageInside {
            createSingleFileMacro(NoAuth("https://github.com/jaagr/polybar"))
        }

        wait.until { driver.findElementByClassName("git4c-singlefile-app") }

    }

    @Test
    fun `Single file repository with SSHAuth should be created`() {

        driver.createPageInside {
            createSingleFileMacro(SSHKey("git@github.com:jereksel/EmptyNARepo.git", Paths.get("src", "test", "resources", "keys", "seleniumtest").toFile().readText()))
        }

        wait.until { driver.findElementByClassName("git4c-singlefile-app") }

    }

    @Test
    fun `Single file repository with NoAuth repository and line range selected should be created`() {
        driver.createPageInside {
            createSingleFileMacro(
                    repoType = NoAuth("https://github.com/jaagr/polybar"),
                    file = ".travis.yml",
                    fileRange = Pair(2,10)

            )
        }

        wait.until { driver.findElementByClassName("git4c-singlefile-app") }

    }


}