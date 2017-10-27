package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v9;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Table("MacroSettings")
@Preload
public interface MacroSettingsEntityBefore extends Entity {

    String getUuid();

    void setUuid(String uuid);

    String getBranch();

    void setBranch(String branch);

    String getRepository();

    void setRepository(String repository);

    String getDefaultDocItem();

    void setDefaultDocItem(String defaultDocItem);

    String getMethod();

    void setMethod(String method);

}
