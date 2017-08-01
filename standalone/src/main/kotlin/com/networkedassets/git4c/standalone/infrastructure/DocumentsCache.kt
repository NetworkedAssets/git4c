package com.networkedassets.git4c.standalone.infrastructure

import com.networkedassets.git4c.core.datastore.DocumentsViewCache
import com.networkedassets.git4c.data.macro.documents.DocumentationMacro
import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem

class DocumentsCache: HashMapCache<DocumentationMacro>(), DocumentsViewCache