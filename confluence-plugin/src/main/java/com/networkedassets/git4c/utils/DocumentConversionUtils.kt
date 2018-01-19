package com.networkedassets.git4c.utils

import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.core.datastore.extractors.EmptyExtractorData
import com.networkedassets.git4c.core.datastore.extractors.ExtractorData

object DocumentConversionUtils {

    fun idOfConvertedDocument(repositoryPath: String, repositoryBranch: String, path: String, extractorUuid: String?) = repositoryPath + "_" + repositoryBranch + "_" + path + "_" + (extractorUuid ?: EmptyExtractorData.uuid)

    fun idOfConvertedDocument(repositoryPath: String, repositoryBranch: String, path: String, extractor: ExtractorData?) = idOfConvertedDocument(repositoryPath, repositoryBranch, path, extractor?.uuid)

    fun idOfConvertedDocument(repositoryPath: String, repositoryBranch: String, it: ImportedFileData, extractor: ExtractorData?) = idOfConvertedDocument(repositoryPath, repositoryBranch, it.path, extractor)

}