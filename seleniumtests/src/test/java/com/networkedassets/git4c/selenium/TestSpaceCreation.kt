package com.networkedassets.git4c.selenium

import com.networkedassets.git4c.selenium.confluence.ConfluenceApi
import org.junit.Test

class TestSpaceCreation {

    @Test
    fun createSpace() {

        val confluenceApi = ConfluenceApi("http://pc-aressel:1990/confluence", "admin", "admin")

        val space = confluenceApi.getSpaceByName("My new space") ?: confluenceApi.createSpace("My new space")

        val page = confluenceApi.getPageByName(space, "My new page") ?: confluenceApi.createPage(space, "My new page")

        confluenceApi.removePage(page)
        confluenceApi.removeSpace(space)

        println(space)
        println(page)

//        val spaceId = ConfluenceUtils.createSpace("http://pc-aressel:1990/confluence", "admin", "admin", "SeleniumTests")
//
//        val pageId = ConfluenceUtils.createPage("http://pc-aressel:1990/confluence", "admin", "admin", spaceId, "New page")

    }

}