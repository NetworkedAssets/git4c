package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.before;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

@Table("MacroSettings")
public interface DocumentationsMacroSettingsBefore extends Entity {

    String getUuid();

    void setUuid(String uuid);

    String getPath();

    void setPath(String path);

    String getBranch();

    void setBranch(String branch);

    AuthEntityBefore getAuth();

    void setAuth(AuthEntityBefore auth);

    String getSecurityKey();

    void setSecurityKey(String key);

    String getDefaultDocItem();

    void setDefaultDocItem(String defaultDocItem);
}
