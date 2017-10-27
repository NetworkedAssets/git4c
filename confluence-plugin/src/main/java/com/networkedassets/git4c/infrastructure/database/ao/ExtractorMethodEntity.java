package com.networkedassets.git4c.infrastructure.database.ao;

import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Table("Method")
@Preload
public interface ExtractorMethodEntity extends ExtractorEntity {

    String getMethod();
    void setMethod(String method);

}
