package com.networkedassets.git4c.selenium

import com.networkedassets.git4c.selenium.codeeditor.CodeEditorTest
import com.networkedassets.git4c.selenium.confluence.ConfluenceApi
import com.networkedassets.git4c.selenium.publicaccess.PublicAccessMultiFileUsageTest
import com.networkedassets.git4c.selenium.publicaccess.PublicAccessSingleFileUsageTest
import com.networkedassets.git4c.selenium.publicaccess.PublicAccessTests
import com.networkedassets.git4c.selenium.repository.CreateRepositoryTests
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

typealias MultiFileCreationTest = com.networkedassets.git4c.selenium.multifile.MultiFileCreationTest
typealias MultiFileUsageTest = com.networkedassets.git4c.selenium.multifile.MultiFileUsageTest
typealias SingleFileCreationTest = com.networkedassets.git4c.selenium.singlefile.SingleFileCreationTest
typealias SingleFileUsageTest = com.networkedassets.git4c.selenium.singlefile.SingleFileUsageTest

@RunWith(Suite::class)
@SuiteClasses(
        //These tests remove all Confluence data (by testing clearing functionality)
//        AdminPanelTests::class,
        MultiFileCreationTest::class,
        MultiFileUsageTest::class,
        CreateRepositoryTests::class,
        SingleFileCreationTest::class,
        SingleFileUsageTest::class,

        PublicAccessSingleFileUsageTest::class,
        PublicAccessMultiFileUsageTest::class,
        PublicAccessTests::class,

        CodeEditorTest::class
)
class SeleniumSuite {

    companion object {

        @BeforeClass
        @JvmStatic
        fun createSpace() {
            createPrivateSpace()
            createPublicSpace()
        }

        fun createPrivateSpace() {

            val config = TestConfig.INSTANCE

            val spaceName = config.spaceName

            val api = ConfluenceApi(config.url, config.username, config.password)

            val space = api.getSpaceByName(spaceName)

            if (space != null) {
                api.removeSpace(space)
            }

            api.createSpace(spaceName)

        }

        fun createPublicSpace() {

            val config = TestConfig.INSTANCE

            val spaceName = config.publicSpaceName

            val api = ConfluenceApi(config.url, config.username, config.password)

            val space = api.getSpaceByName(spaceName)

            if (space != null) {
                api.removeSpace(space)
            }

            val s = api.createSpace(spaceName)

            api.enableAnonymousAccessToSpace(s)

        }

    }
}