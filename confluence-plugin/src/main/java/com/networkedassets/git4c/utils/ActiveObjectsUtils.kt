package com.networkedassets.git4c.utils

import com.atlassian.activeobjects.external.ActiveObjects
import net.java.ao.Query
import net.java.ao.RawEntity

object ActiveObjectsUtils {
    inline fun <reified T> ActiveObjects.findByUuid(uuid: String): T?
            where T : RawEntity<Int> {
        return this.find(T::class.java, Query.select().where("UUID = ?", uuid)).firstOrNull()
    }
}

