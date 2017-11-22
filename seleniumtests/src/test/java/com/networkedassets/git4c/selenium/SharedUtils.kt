package com.networkedassets.git4c.selenium

import com.networkedassets.git4c.selenium.utils.WebdriverExtensions.findElementById
import com.sun.org.apache.xerces.internal.impl.xpath.XPath
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.Select
import java.util.concurrent.TimeUnit

interface SharedUtils {

    fun WebDriver.createPage(pageName: String, creationFun: WebDriver.() -> Unit) {

        val driver = this

        val wait = FluentWait<WebDriver>(driver)
                .withTimeout(15, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(org.openqa.selenium.NoSuchElementException::class.java)

        val createPage = driver.findElementById("quick-create-page-button")

        createPage.click()

        wait.until { driver.findElementById("content-title") }.sendKeys(pageName)

        creationFun()

        wait.until { driver.findElementById("rte-button-publish") }

        //On FF sometimes title is not set the first time
        wait.until { driver.findElementById("content-title") }.sendKeys(pageName)

        Thread.sleep(2000)

        (driver as JavascriptExecutor).executeScript("""$("#rte-button-publish").click()""")

    }

    fun WebDriver.createPageInside(creationFun: WebDriver.() -> Unit) {

        val driver = this

        val wait = FluentWait<WebDriver>(driver)
                .withTimeout(15, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(org.openqa.selenium.NoSuchElementException::class.java)

        creationFun()

        wait.until { driver.findElementById("rte-button-publish") }

        Thread.sleep(2000)

        (driver as JavascriptExecutor).executeScript("""$("#rte-button-publish").click()""")

    }

    fun WebDriver.createSpace(spaceName: String) {

        val driver = this

        val wait = FluentWait<WebDriver>(driver)
                .withTimeout(15, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(org.openqa.selenium.NoSuchElementException::class.java)

        val fastWait = FluentWait<WebDriver>(driver)
                .withTimeout(4, TimeUnit.SECONDS)
                .pollingEvery(500, TimeUnit.MILLISECONDS)
                .ignoring(org.openqa.selenium.NoSuchElementException::class.java, StaleElementReferenceException::class.java)


        wait.until(ExpectedConditions.elementToBeClickable(driver.findElementById("space-menu-link"))).click()

        wait.until { it.findElement(By.id("create-space-header")) }.click()

        try {
            fastWait.until { driver.findElementById("space-welcome-dialog").findElement(By.xpath("//button[contains(.,'Create')]")) }.click()
        } catch (ignored: Exception) {
//            ignored.printStackTrace()
        }

        val dialog = driver.findElementById("create-dialog")

        wait.until { dialog.findElement(By.xpath("//button[contains(.,'Next')]")) }.click()

        try {
            fastWait.until { dialog.findElement(By.xpath("//button[contains(.,'Next')]")) }.click()
        } catch (ignored: Exception) {
//            ignored.printStackTrace()
        }

        dialog.findElement(By.xpath("//label[contains(.,'Space name')]")).findElement(By.xpath("..")).findElement(By.xpath("input")).sendKeys(spaceName)

        val spaceKeyInput = dialog.findElement(By.xpath("//label[contains(.,'Space key')]")).findElement(By.xpath("..")).findElement(By.xpath("input"))

        spaceKeyInput.clear()
        spaceKeyInput.sendKeys(spaceName)

//        dialog.findElement(By.xpath("//label[contains(.,'Space key')]")).findElement(By.xpath("..")).findElement(By.xpath("input")).sendKeys(spaceName)

        val saveButton = driver.findElementById("create-dialog").findElements(By.className("create-dialog-create-button"))[1]

        wait.until {
            saveButton.isEnabled
        }

        saveButton.click()

        wait.until {
            val element = driver.findElement(By.xpath("//*[contains(.,'$spaceName Home')]"))
            println(element)
            element
        }

        wait.until {
            driver.findElement(By.xpath("//p[contains(.,'Welcome to your new space!')]"))
        }

    }

    fun WebDriver.setCustomRepository(repoType: RepoType, name: String) {

        val driver = this

        val wait = FluentWait<WebDriver>(driver)
                .withTimeout(5, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(org.openqa.selenium.NoSuchElementException::class.java)

//
//        git4c-multi_file_dialog-add_repository-button

//        try {
//            wait.until { driver.findElementById("git4c-multi_file_dialog-add_repository-button") }.click()
//        } catch (e: Exception) {
////            wait.until { driver.findElementById("git4c-singlefiledialog-add-custom-repository") }.click()
//            try {
//                wait.until { driver.findElementById("git4c-single-file-dialog-content").findElement(By.className("aui-iconfont-add")) }.click()
//            } catch (e: Exception) {
//
//            }
//        }

        wait.until { driver.findElementById("doc_macro-repo_name") }.sendKeys(name)

        driver.findElementById("doc_macro-repo_url").sendKeys(repoType.url)

        Select(driver.findElementById("doc_macro-auth_type")).selectByVisibleText(repoType.spinnerName)

        when (repoType) {
            is NoAuth -> {
            }
            is SSHKey -> {
                driver.findElementById("doc_macro-sshkey").sendKeys(repoType.key)
            }
        }

        driver.findElementById("custom_repository_dialog-close-button").click()

        Thread.sleep(5000)

    }

    fun WebDriver.setRootDir(){
        val driver = this

        val wait = FluentWait<WebDriver>(driver)
                .withTimeout(5, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(org.openqa.selenium.NoSuchElementException::class.java)
        val rootList = driver.findElement(By.xpath("//*[@id=\"git4c-single-dialog-tree-div\"]/span/ol"))
        val elements = rootList.findElements(By.tagName("span"))
        val dir = elements[0].findElement(By.tagName("li")).findElement(By.tagName("div")).findElement(By.tagName("a"))
        wait.until { dir.click() }
        val saveButton = driver.findElement(By.xpath("//*[@id=\"dialog-next-button\"]"))
        wait.until { saveButton.click() }

        wait.until { driver.findElement(By.className("select2-search-choice")) }




    }

}