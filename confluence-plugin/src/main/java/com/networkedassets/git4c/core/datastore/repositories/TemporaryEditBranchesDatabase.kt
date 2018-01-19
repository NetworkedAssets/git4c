package com.networkedassets.git4c.core.datastore.repositories

import com.atlassian.activeobjects.tx.Transactional
import com.networkedassets.git4c.core.bussiness.Database
import com.networkedassets.git4c.data.TemporaryEditBranch

@Transactional
interface TemporaryEditBranchesDatabase: Database<TemporaryEditBranch> {
}

fun TemporaryEditBranchesDatabase.get(repositoryId: String, pageId: String) = get("${repositoryId}_$pageId")
fun TemporaryEditBranchesDatabase.put(repositoryId: String, pageId: String, temporaryBranch: TemporaryEditBranch) = put("${repositoryId}_$pageId", temporaryBranch)
