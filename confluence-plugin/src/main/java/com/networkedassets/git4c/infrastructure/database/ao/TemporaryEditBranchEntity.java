package com.networkedassets.git4c.infrastructure.database.ao;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

@Table("TemporaryEB")
public interface TemporaryEditBranchEntity extends Entity {

    String getUuid();
    void setUuid(String uuid);

    String getName();
    void setName(String name);
}
