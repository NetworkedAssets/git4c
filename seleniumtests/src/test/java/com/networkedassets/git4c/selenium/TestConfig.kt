package com.networkedassets.git4c.selenium

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import java.io.File
import java.net.URL

class TestConfig(
        val junitData: Collection<Array<Any>>,
        val url: String,
        val username: String,
        val password: String,
        val spaceName: String = "Selenium tests",
        val publicSpaceName: String = "Selenium tests public"
) {

    companion object {
        val INSTANCE by lazy {

            val confluenceUsername = (System.getenv("CONFLUENCE_USERNAME")) ?: throw RuntimeException("CONFLUENCE_USERNAME is not set")
            val confluencePassword = (System.getenv("CONFLUENCE_PASSWORD")) ?: throw RuntimeException("CONFLUENCE_PASSWORD is not set")
            val firefoxLocation = (System.getenv("FIREFOX_LOCATION") ?: "").trim()
            val chromeLocation = (System.getenv("CHROME_LOCATION") ?: "").trim()
            val ieLocation = (System.getenv("IE_LOCATION") ?: "").trim()
            val confluenceLocation = (System.getenv("CONFLUENCE_LOCATION") ?: "").trim()

            val firefoxSupplier: Callback<WebDriver>? = {

                val file = File(firefoxLocation)

                if (firefoxLocation.isEmpty()) {
                    null
                } else if (file.exists()) {
                    //It's driver file
                    val geckoDriverFile = File(file.absolutePath) //path to the chromedriver.exe so downloaded
                    System.setProperty("webdriver.gecko.driver", geckoDriverFile.absolutePath)
                    Callback { FirefoxDriver() as WebDriver }
                } else if (firefoxLocation.split(":").size == 2) {
                    //It's ip:port
                    Callback { RemoteWebDriver(URL("http://$firefoxLocation/wd/hub"), DesiredCapabilities.firefox()) as WebDriver }
                } else {
                    throw RuntimeException("Wrong firefox location format $firefoxLocation")
                }

            }()

            val chromeSupplier: Callback<WebDriver>? = {

                val file = File(chromeLocation)

                if (chromeLocation.isEmpty()) {
                    null
                } else if (file.exists()) {
                    //It's driver file
                    val chromeDriverFile = File(file.absolutePath) //path to the chromedriver.exe so downloaded
                    System.setProperty("webdriver.chrome.driver", chromeDriverFile.absolutePath)
                    Callback { ChromeDriver() as WebDriver }
                } else if (chromeLocation.split(":").size == 2) {
                    //It's ip:port
                    val capacity = DesiredCapabilities.chrome()
//                    capacity.
                    val options = ChromeOptions()
                    options.addArguments("--headless")
                    options.addArguments("--disable-gpu")
                    capacity.setCapability(ChromeOptions.CAPABILITY, options)
                    Callback { RemoteWebDriver(URL("http://$chromeLocation/wd/hub"), capacity) as WebDriver }
                } else {
                    throw RuntimeException("Wrong chrome location format $chromeLocation")
                }

            }()

            val ieSupplier: Callback<WebDriver>? = {

                val file = File(ieLocation)

                if (ieLocation.isEmpty()) {
                    null
                } else if (file.exists()) {
                    //It's driver file
                    val ieDriverFile = File(file.absolutePath) //path to the chromedriver.exe so downloaded
                    System.setProperty("webdriver.ie.driver", ieDriverFile.absolutePath)
                    Callback { InternetExplorerDriver() as WebDriver }
                } else if (ieLocation.split(":").size == 2) {
                    //It's ip:port
                    Callback { RemoteWebDriver(URL("http://$ieLocation/wd/hub"), DesiredCapabilities.internetExplorer()) as WebDriver }
                } else {
                    throw RuntimeException("Wrong internet explorer location format $ieLocation")
                }

            }()

            val list: List<Pair<Callback<WebDriver>, String>>

            list = listOf(
                    Pair(chromeSupplier, "chrome"),
                    Pair(firefoxSupplier, "firefox"),
                    Pair(ieSupplier, "Internet Explorer")
            )
                    .mapNotNull {
                        val first = it.first
                        if (first == null) {
                            null
                        } else {
                            Pair(first, it.second)
                        }
                    }

            TestConfig(
                    list.map { it.toList().toTypedArray() },
                    confluenceLocation,
                    confluenceUsername,
                    confluencePassword
            )

        }

    }
}