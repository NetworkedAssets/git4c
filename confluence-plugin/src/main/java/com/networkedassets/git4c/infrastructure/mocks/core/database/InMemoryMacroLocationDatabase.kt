package com.networkedassets.git4c.infrastructure.mocks.core.database

import com.networkedassets.git4c.core.datastore.repositories.MacroLocationDatabase
import com.networkedassets.git4c.data.MacroLocation
import com.networkedassets.git4c.infrastructure.mocks.generic.InMemoryCache

class InMemoryMacroLocationDatabase : InMemoryCache<MacroLocation>(), MacroLocationDatabase
