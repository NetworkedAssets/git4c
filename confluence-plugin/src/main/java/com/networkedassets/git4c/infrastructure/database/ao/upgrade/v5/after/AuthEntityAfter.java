package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.after;

import net.java.ao.Entity;
import net.java.ao.Polymorphic;
import net.java.ao.Preload;

@Polymorphic
@Preload
public interface AuthEntityAfter extends Entity {

    String getUuid();

    void setUuid(String uuid);

    String getPath();

    void setPath(String path);

    String getSecurityKey();

    void setSecurityKey(String securityKey);

}
