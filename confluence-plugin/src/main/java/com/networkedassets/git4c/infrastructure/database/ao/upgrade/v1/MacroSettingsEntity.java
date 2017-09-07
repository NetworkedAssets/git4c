package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v1;

import com.networkedassets.git4c.infrastructure.database.ao.RepositoryEntity;
import net.java.ao.Entity;
import net.java.ao.schema.Table;

@Table("MacroSettings")
public interface MacroSettingsEntity extends Entity {

    String getUuid();

    void setUuid(String uuid);

    String getPath();

    void setPath(String path);

    String getBranch();

    void setBranch(String branch);

    String getGlob();

    void setGlob(String glob);

    RepositoryEntity getAuth();

    void setAuth(RepositoryEntity auth);

    String getSecurityKey();

    void setSecurityKey(String key);

    String getDefaultDocItem();

    void setDefaultDocItem(String defaultDocItem);
}
