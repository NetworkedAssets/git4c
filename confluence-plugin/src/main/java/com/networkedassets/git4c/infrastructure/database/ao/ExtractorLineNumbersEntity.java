package com.networkedassets.git4c.infrastructure.database.ao;

import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Table("LineNumbers")
@Preload
public interface ExtractorLineNumbersEntity extends ExtractorEntity {

    int getStartLine();
    void setStartLine(int line);

    int getEndLine();
    void setEndLine(int line);

}
