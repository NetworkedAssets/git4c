package com.networkedassets.git4c.selenium.adminpanel

import com.networkedassets.git4c.selenium.BaseSeleniumTest
import com.networkedassets.git4c.selenium.NoAuth
import org.junit.Test
import org.openqa.selenium.By
import kotlin.test.assertEquals

class AdminPanelTests : BaseSeleniumTest() {

    @Test
    fun addPredefinedRepository() {

        driver.get(adminPanelLocation)

        wait.until { driver.findElementsByXPath("""id('predefined_repo_table_body')/tr""").isNotEmpty() }

        Thread.sleep(1000)

        val numberOfPredefines = driver.findElementsByXPath("""id('predefined_repo_table_body')/tr""").size

        wait.until { driver.findElementById("add_repository-button").isDisplayed }

        driver.findElementsByClassName("aui-message").filter { it.isDisplayed }.forEach { it.findElement(By.className("icon-close")).click() }

        wait.until { driver.findElementById("add_repository-button") }.click()

        driver.setCustomRepository(NoAuth("https://github.com/jaagr/polybar"), "Test noauth")

        wait.until { driver.findElementsByXPath("""id('predefined_repo_table_body')/tr""").size == numberOfPredefines + 1 }

        val tr = driver.findElementsByXPath("""id('predefined_repo_table_body')/tr""").last()

        val td = tr.findElements(By.tagName("td"))

        assertEquals("https://github.com/jaagr/polybar", td[0].text)
        assertEquals("Test noauth", td[1].text)
        assertEquals("NOAUTH", td[2].text)

        td[3].findElements(By.tagName("a"))[1].click()

        wait.until { driver.findElementById("remove_repository-remove_button") }.click()

        wait.until { driver.findElementsByXPath("""id('predefined_repo_table_body')/tr""").size == numberOfPredefines }

        val tr2 = driver.findElementsByXPath("""id('predefined_repo_table_body')/tr""")

        assertEquals(numberOfPredefines, tr2.size)

    }

    @Test
    fun customFilterTest() {

//        login()
        driver.get(adminPanelLocation)

        //Restore default filters before testing

        wait.until { driver.findElementById("restore_default_globs-button") }.click()

        wait.until { driver.findElementById("restore_default_globs-confirm_button").isEnabled }

        driver.findElementById("restore_default_globs-confirm_button").click()

        wait.until { driver.findElementById("add_glob-button") }.click()

        wait.until { driver.findElementById("doc_macro-glob_name") }.sendKeys("My glob")

        driver.findElementById("doc_macro-glob_pattern").sendKeys("README.md")

        driver.findElementById("custom_glob_dialog-close-button").click()

        wait.until { driver.findElementsByXPath("""id('predefined_glob_table_body')/tr""").size == 6 }

        val lasttr = driver.findElementsByXPath("""id('predefined_glob_table_body')/tr""").last()

        val tds = lasttr.findElements(By.tagName("td"))

        assertEquals("My glob", tds[0].text)
        assertEquals("README.md", tds[1].text)

        driver.findElementsByClassName("aui-message").filter { it.isDisplayed }.forEach { it.findElement(By.className("icon-close")).click() }

        tds[2].findElement(By.tagName("a")).click()

        wait.until { driver.findElementById("remove_glob-remove_button") }.click()

        wait.until { driver.findElementsByXPath("""id('predefined_glob_table_body')/tr""").size == 5 }

    }

    @Test
    fun cleanAndRestoreDataTest() {

        driver.get(adminPanelLocation)

        wait.until { driver.findElementsByXPath("""id('predefined_glob_table_body')/tr""").size >= 0 }

        wait.until { driver.findElementById("remove_data-button") }.click()

        wait.until { driver.findElementById("clean_data_warning-clean_button") }.click()

        Thread.sleep(5000)

        wait.until { driver.findElementById("clean_data_warning-clean_button") }.click()

        wait.until { driver.findElementsByXPath("""id('predefined_glob_table_body')/tr""").size == 0 }

        wait.until { driver.findElementById("restore_default_globs-button") }.click()

        wait.until { driver.findElementById("restore_default_globs-confirm_button").isEnabled }
        driver.findElementById("restore_default_globs-confirm_button").click()

        wait.until { driver.findElementsByXPath("""id('predefined_glob_table_body')/tr""").size == 5 }

    }
}
