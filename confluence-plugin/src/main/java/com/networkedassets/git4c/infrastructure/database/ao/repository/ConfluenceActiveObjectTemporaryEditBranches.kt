package com.networkedassets.git4c.infrastructure.database.ao.repository

import com.atlassian.activeobjects.external.ActiveObjects
import com.networkedassets.git4c.core.datastore.repositories.TemporaryEditBranchesDatabase
import com.networkedassets.git4c.data.TemporaryEditBranch
import com.networkedassets.git4c.infrastructure.database.ao.TemporaryEditBranchEntity
import com.networkedassets.git4c.utils.ActiveObjectsUtils.findByUuid

class ConfluenceActiveObjectTemporaryEditBranches(val ao: ActiveObjects) : TemporaryEditBranchesDatabase {

    override fun isAvailable(uuid: String) = getFromDatabase(uuid) != null

    override fun get(uuid: String) = getFromDatabase(uuid)?.convert()

    override fun getAll() = ao.find(TemporaryEditBranchEntity::class.java).map { it.convert() }

    override fun put(uuid: String, data: TemporaryEditBranch) {
        val entity = getFromDatabase(uuid) ?: ao.create(TemporaryEditBranchEntity::class.java)

        entity.uuid = uuid
        entity.name = data.name

        entity.save()
    }

    override fun remove(uuid: String) {
        getFromDatabase(uuid)?.let { ao.delete(it) }
    }

    override fun removeAll() {
        ao.find(TemporaryEditBranchEntity::class.java).forEach { ao.delete(it) }
    }

    private fun getFromDatabase(uuid: String) = ao.findByUuid<TemporaryEditBranchEntity>(uuid)

    private fun TemporaryEditBranchEntity.convert() = TemporaryEditBranch(uuid, name)

}
