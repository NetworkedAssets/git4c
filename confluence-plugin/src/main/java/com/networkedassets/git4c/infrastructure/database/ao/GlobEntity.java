package com.networkedassets.git4c.infrastructure.database.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Table("Glob")
@Preload
public interface GlobEntity extends Entity {

    String getUuid();

    void setUuid(String uuid);

    String getMacro();

    void setMacro(String macro);

    String getGlob();

    void setGlob(String string);
}
