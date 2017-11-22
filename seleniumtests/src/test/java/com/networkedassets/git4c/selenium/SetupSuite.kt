package com.networkedassets.git4c.selenium

import com.networkedassets.git4c.selenium.setup.InstallPlugin
import com.networkedassets.git4c.selenium.setup.SetupConfluence
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(SetupConfluence::class, InstallPlugin::class)
class SetupSuite