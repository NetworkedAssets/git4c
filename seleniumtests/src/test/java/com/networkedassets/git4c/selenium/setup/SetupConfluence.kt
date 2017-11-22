package com.networkedassets.git4c.selenium.setup

import org.junit.Test
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.FluentWait
import java.net.URL
import java.util.concurrent.TimeUnit

class SetupConfluence {


    @Test
    fun setupConfluence() {
        val confluenceLocation = """http://localconfluence:8090/"""
        val driver = RemoteWebDriver(URL("http://127.0.0.1:4001/wd/hub"), DesiredCapabilities.chrome())

        //https://developer.atlassian.com/platform/marketplace/timebomb-licenses-for-testing-server-apps/
        val key = """AAACLg0ODAoPeNqNVEtv4jAQvudXRNpbpUSEx6FIOQBxW3ZZiCB0V1WllXEG8DbYke3A8u/XdUgVQ
yg9ZvLN+HuM/e1BUHdGlNvuuEHQ73X73Y4bR4nbbgU9ZwFiD2IchcPH+8T7vXzuej9eXp68YSv45
UwoASYhOeYwxTsIE7RIxtNHhwh+SP3a33D0XnntuxHsIeM5CIdwtvYxUXQPoRIF6KaC0FUGVlEB3
v0hOAOWYiH9abFbgZith3i34nwOO65gsAGmZBhUbNC/nIpjhBWEcefJWelzqIDPWz/OtjmXRYv2X
yqwnwueFkT57x8e4cLmbCD1QnX0UoKQoRc4EUgiaK4oZ2ECUrlZeay75sLNs2JDmZtWR8oPCfWZG
wHAtjzXgIo0SqmZiKYJmsfz8QI5aI+zApuq6fqJKVPAMCPnNpk4LPW6kBWgkZb+kQAzzzS2g6Dnt
e69Tqvsr4SOskIqEFOeggz1v4zrHbr0yLJR8rU64FpQpVtBy1mZxM4CnHC9Faf8tKMnTF1AiXORF
ixyQaWto3RZ+ncWLXtMg6EnKZZRpmQNb2R8tnJXFulCfXmXLry7TrHBWn2HNVyH8WYxj9AzmsxiN
L/R88Xg6rA1lVs4QpO5titxhplJcCY2mFFZLutAZVhKipm15/VhJx36YVqyN8YP7IaGC1+lwnJ7Q
5pJpNmxk5hP3qovutY8Pi4E2WIJ59esnr1p+T6eD67teBVCHf+ga+ho4/4D9YItZDAsAhQ5qQ6pA
SJ+SA7YG9zthbLxRoBBEwIURQr5Zy1B8PonepyLz3UhL7kMVEs=X02q6"""


        driver.get(confluenceLocation)

        val wait = FluentWait<WebDriver>(driver)
                .withTimeout(10, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(org.openqa.selenium.NoSuchElementException::class.java)

        //First screen (setup type)

        //Newest Confluence
//        driver.findElementByXPath("""//div[@data-setup-type="install"]""").click()

        driver.findElementByXPath("""//div[@setup-type="install"]""").click()

        driver.findElementById("setup-next-button").click()

        //Second screen (License screen)

        driver.findElementById("confLicenseString").sendKeys(key)

        driver.findElementById("setup-next-button").click()

        //Third screen (User management)

        driver.findElementById("internal").click()

        //Fourth screen (Admin account)

        driver.findElementById("username").clear()
        driver.findElementById("username").sendKeys("admin")
        driver.findElementById("fullName").sendKeys("admin")
        driver.findElementById("email").sendKeys("admin@admin.admin")
        driver.findElementById("password").sendKeys("admin")
        driver.findElementById("confirm").sendKeys("admin")
        driver.findElementById("setup-next-button").click()

        driver.findElementByXPath("""//a[contains(text(), 'Start')]""").click()

        driver.findElementById("grow-intro-video-skip-button").click()

        wait.until { driver.findElementByXPath("""//button[contains(text(), 'Skip')]""") }.click()

        wait.until { driver.findElementById("grow-intro-space-name") }.sendKeys("Test")

        driver.findElementById("grow-intro-create-space").click()

    }
}