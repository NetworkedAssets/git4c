package com.networkedassets.git4c.core.business

interface SpaceManager {
    fun getAllSpaceKeys(): List<String>
    fun getSpace(spaceKey: String): Space?
}