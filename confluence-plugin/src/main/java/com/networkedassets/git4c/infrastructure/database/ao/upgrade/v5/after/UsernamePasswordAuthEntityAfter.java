package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.after;

import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Table("UserPassAuth")
@Preload
public interface UsernamePasswordAuthEntityAfter extends AuthEntityAfter {

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

}
