package com.networkedassets.git4c.standalone.infrastructure

import com.networkedassets.git4c.core.datastore.MacroSettingsRepository
import com.networkedassets.git4c.data.macro.MacroSettings

class SettingsDB: HashMapDatabase<MacroSettings>(), MacroSettingsRepository