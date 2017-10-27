package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v9;

import net.java.ao.Entity;
import net.java.ao.Polymorphic;
import net.java.ao.Preload;

@Polymorphic
@Preload
public interface ExtractorEntity extends Entity {

    String getUuid();

    void setUuid(String uuid);

}
