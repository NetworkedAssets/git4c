package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5;

import net.java.ao.Entity;
import net.java.ao.OneToOne;
import net.java.ao.Polymorphic;
import net.java.ao.Preload;

@Polymorphic
@Preload
public interface AuthEntity extends Entity {

    @OneToOne
    DocumentationsMacroSettings getMacroSettings();

    String getUuid();

    void setUuid(String uuid);

    String getPath();

    void setPath(String path);

    String getSecurityKey();

    void setSecurityKey(String securityKey);

}
