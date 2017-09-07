package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.after;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

@Table("MacroSettings")
public interface DocumentationsMacroSettingsAfter extends Entity {

    String getUuid();

    void setUuid(String uuid);

    String getPath();

    void setPath(String path);

    String getBranch();

    void setBranch(String branch);

    String getRepository();

    void setRepository(String repository);

    String getSecurityKey();

    void setSecurityKey(String key);

    String getDefaultDocItem();

    void setDefaultDocItem(String defaultDocItem);
}
