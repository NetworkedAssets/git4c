package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v4;

import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v2.AuthEntity;
import net.java.ao.Entity;
import net.java.ao.OneToMany;
import net.java.ao.schema.Table;

@Table("MacroSettings")
public interface DocumentationsMacroSettingsBefore extends Entity {

    String getUuid();

    void setUuid(String uuid);

    String getPath();

    void setPath(String path);

    String getBranch();

    void setBranch(String branch);

    @OneToMany(reverse = "getMacroSettings")
    GlobEntity[] getGlobs();

    AuthEntity getAuth();

    void setAuth(AuthEntity auth);

    String getSecurityKey();

    void setSecurityKey(String key);

    String getDefaultDocItem();

    void setDefaultDocItem(String defaultDocItem);
}
