package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v4;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

@Table("Glob")
public interface GlobEntityAfter extends Entity {

    String getUuid();

    void setUuid(String uuid);

    String getMacro();

    void setMacro(String macroUuid);

    String getGlob();

    void setGlob(String string);
}
