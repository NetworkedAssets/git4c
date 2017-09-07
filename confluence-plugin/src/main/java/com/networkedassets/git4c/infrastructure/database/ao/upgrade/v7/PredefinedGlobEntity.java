package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v7;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Table("PredefinedGlob")
@Preload
public interface PredefinedGlobEntity extends Entity {

    String getUuid();

    void setUuid(String uuid);

    String getGlob();

    void setGlob(String string);

    String getName();

    void setName(String name);
}
