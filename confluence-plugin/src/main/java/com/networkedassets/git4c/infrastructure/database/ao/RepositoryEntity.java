package com.networkedassets.git4c.infrastructure.database.ao;

import net.java.ao.Entity;
import net.java.ao.Polymorphic;
import net.java.ao.Preload;

@Polymorphic
@Preload
public interface RepositoryEntity extends Entity {

    String getUuid();

    void setUuid(String uuid);

    String getPath();

    void setPath(String path);

    String getSecurityKey();

    void setSecurityKey(String securityKey);
}