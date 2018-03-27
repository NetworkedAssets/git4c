package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v10;

import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Table("UserPassAuth")
@Preload
public interface RepositoryWithUsernameAndPasswordEntityBefore extends RepositoryEntityBefore {

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

}
