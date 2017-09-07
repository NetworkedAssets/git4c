package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5;

import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Table("UserPassAuth")
@Preload
public interface UsernamePasswordAuthEntity extends AuthEntity {

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

}
