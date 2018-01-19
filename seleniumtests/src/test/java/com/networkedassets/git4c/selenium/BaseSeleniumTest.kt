package com.networkedassets.git4c.selenium

import com.networkedassets.git4c.selenium.annotations.Public
import com.networkedassets.git4c.selenium.confluence.ConfluenceApi
import com.networkedassets.git4c.selenium.confluence.Page
import org.junit.Before
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.rules.TestName
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.junit.runners.model.Statement
import org.openqa.selenium.*
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.FluentWait
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

@RunWith(Parameterized::class)
open class BaseSeleniumTest : SharedUtils {

    @Parameter(0)
    lateinit var driverSupplier: Callback<RemoteWebDriver>

    @Parameter(1)
    lateinit var name: String

    //    @Parameter(2)
    val confluenceLocation by lazy { TestConfig.INSTANCE.url }

    @Suppress("RedundantVisibilityModifier")
    @Rule
    @JvmField
    public var testName = TestName()

    @Suppress("RedundantVisibilityModifier")
    @Rule
    @JvmField
    //  public var rule = RuleChain.outerRule(CreatePageRule()).around(RetryRule()).around(ScreenshotTestRule())
    public var rule = RuleChain.outerRule(RetryAndScreenshotAtFailtureTestRule()).around(CreatePageRule())

    val adminPanelLocation
        get() = "$confluenceLocation/plugins/servlet/git4c/admin"

    lateinit var driver: WebDriver

    lateinit var fastWait: FluentWait<WebDriver>
    lateinit var wait: FluentWait<WebDriver>

    companion object {
        @Parameters(name = "{1}")
        @JvmStatic
        fun data(): Collection<Array<Any>> {

            val config = TestConfig.INSTANCE

            return config.junitData
        }
    }

    protected lateinit var page: Page

    inner class CreatePageRule : TestRule {
        override fun apply(base: Statement, description: Description): Statement {
            return object : Statement() {
                override fun evaluate() {

                    val methodName = description.methodName
                    val className = description.testClass.simpleName

                    val pageName = "$className-$methodName"

                    val config = TestConfig.INSTANCE

                    val api = ConfluenceApi(config)

                    val public = description.getAnnotation(Public::class.java) != null

                    val spaceName = if (public) config.publicSpaceName else config.spaceName

                    val space = api.getSpaceByName(spaceName) ?: throw RuntimeException("Space ${spaceName} doesn't exists")

                    val page = {
                        val page = api.getPageByName(space, pageName)

                        if (page != null) {
                            api.removePage(page)
                        }

                        api.createPage(space, pageName)
                    }()

                    this@BaseSeleniumTest.page = page

                    try {
                        base.evaluate()
                    } catch (t: Throwable) {
                        //Something went wrong. Remove page and pass exception to next rules
                        api.removePage(page)
                        throw t
                    }

                }
            }
        }
    }

    inner class RetryAndScreenshotAtFailtureTestRule : TestRule {

        override fun apply(base: Statement, description: Description): Statement {
            return object : Statement() {
                @Throws(Throwable::class)
                override fun evaluate() {
                    var caughtThrowable: Throwable? = null
                    val retryCount = 5

                    for (i in 0 until retryCount) {
                        try {
                            base.evaluate()
                            driver.close()
                            break
                        } catch (t: Throwable) {
                            caughtThrowable = t
                            captureScreenshot(description.displayName, i)
                            if (i != retryCount - 1) {
                                System.err.println("${description.displayName}: run ${i + 1} failed")
                                t.printStackTrace(System.err)
                            } else {
                                System.err.println("${description.displayName}: giving up after $retryCount failures")
                                driver.close()
                                throw caughtThrowable

                            }
                        }
                        driver.close()
                    }

                }

                fun captureScreenshot(fileName: String, tryNr: Int) {
                    try {
                        File("target/surefire-reports/").mkdirs() // Insure directory is there
                        val out = FileOutputStream("target/surefire-reports/screenshot-$fileName-$name-$tryNr.png")
                        out.write((driver as TakesScreenshot).getScreenshotAs<ByteArray>(OutputType.BYTES))
                        out.close()
                    } catch (e: Exception) {
                        // No need to crash the tests if the screenshot fails
                    }

                }
            }
        }
    }


    @Before
    fun setup() {

        val page = page

        driver = driverSupplier.get()

        fastWait = FluentWait<WebDriver>(driver)
                .withTimeout(4, TimeUnit.SECONDS)
                .pollingEvery(500, TimeUnit.MILLISECONDS)
                .ignoring(org.openqa.selenium.NoSuchElementException::class.java, StaleElementReferenceException::class.java)

        wait = FluentWait<WebDriver>(driver)
                .withTimeout(15, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(org.openqa.selenium.NoSuchElementException::class.java, StaleElementReferenceException::class.java)

//        driver.get("$confluenceLocation${page.link}")

        driver.get("$confluenceLocation/pages/editpage.action?pageId=${page.id}")

        login()
    }

    fun login() {

        val config = TestConfig.INSTANCE

        val username = config.username
        val password = config.password

        driver.findElementById("os_username").sendKeys(username)
        driver.findElementById("os_password").sendKeys(password)
        driver.findElementById("loginButton").click()

        wait.until { driver.findElementByClassName("aui-header-logo-device") }

        try {
            driver.findElementByClassName("skip-onboarding").click()
        } catch (ignored: Exception) {
        }
    }

    fun WebDriver.findElementById(s: String) = this.findElement(By.id(s))
    fun WebDriver.findElementByClassName(s: String) = this.findElement(By.className(s))

    fun WebDriver.findElementsByXPath(s: String) = this.findElements(By.xpath(s))

    fun WebDriver.findElementsByTagName(tagName: String) = this.findElements(By.tagName(tagName))

    fun WebDriver.findElementsByClassName(className: String) = this.findElements(By.className(className))
    fun WebDriver.findElementByLinkText(linkText: String) = this.findElement(By.linkText(linkText))

}

