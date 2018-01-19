package com.networkedassets.git4c.infrastructure.database.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Table("MacroLocation")
@Preload
public interface MacroLocationEntity extends Entity {

    String getUuid();

    void setUuid(String uuid);

    String getPage();

    void setPage(String page);

    String getSpace();

    void setSpace(String space);

}
