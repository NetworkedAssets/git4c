package com.networkedassets.git4c.core.bussiness;

import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem;

public interface ConverterPlugin extends Plugin {
    DocumentsItem convert(ImportedFileData fileData);
}