package com.networkedassets.git4c.standalone.infrastructure

import com.networkedassets.git4c.core.datastore.MacroSettingsCache
import com.networkedassets.git4c.data.macro.MacroSettings

class SettingsCache: HashMapCache<MacroSettings>(), MacroSettingsCache