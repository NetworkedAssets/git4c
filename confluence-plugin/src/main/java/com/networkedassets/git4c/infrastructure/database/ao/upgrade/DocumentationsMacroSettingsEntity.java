package com.networkedassets.git4c.infrastructure.database.ao.upgrade;

import com.networkedassets.git4c.infrastructure.database.ao.AuthEntity;
import net.java.ao.Entity;
import net.java.ao.schema.Table;

@Table("MacroSettings")
public interface DocumentationsMacroSettingsEntity extends Entity {
    String getUuid();
    void setUuid(String uuid);

    String getPath();
    void setPath(String path);

    String getBranch();
    void setBranch(String branch);

    String getGlob();
    void setGlob(String glob);

    AuthEntity getAuth();
    void setAuth(AuthEntity auth);

    String getSecurityKey();
    void setSecurityKey(String key);

    String getDefaultDocItem();
    void setDefaultDocItem(String defaultDocItem);
}
