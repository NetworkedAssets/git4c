package com.networkedassets.git4c.selenium.utils

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

object WebdriverExtensions {
    fun WebDriver.findElementById(s: String) = this.findElement(By.id(s))
    fun WebDriver.findElementByClassName(s: String) = this.findElement(By.className(s))

    fun WebDriver.findElementsByXPath(s: String) = this.findElements(By.xpath(s))
}