package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

@Table("MacroSettings")
public interface DocumentationsMacroSettings extends Entity {

    String getUuid();

    void setUuid(String uuid);

    String getPath();

    void setPath(String path);

    String getBranch();

    void setBranch(String branch);

    AuthEntity getAuth();

    void setAuth(AuthEntity auth);

    String getRepository();

    void setRepository(String repository);

    String getSecurityKey();

    void setSecurityKey(String key);

    String getDefaultDocItem();

    void setDefaultDocItem(String defaultDocItem);
}
