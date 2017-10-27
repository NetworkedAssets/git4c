package com.networkedassets.git4c.infrastructure.database.ao;

import net.java.ao.Entity;
import net.java.ao.Polymorphic;
import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Polymorphic
@Preload
public interface ExtractorEntity extends Entity {

    String getUuid();

    void setUuid(String uuid);

}
