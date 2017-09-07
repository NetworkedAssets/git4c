package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v8;

import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Table("UserPassAuth")
@Preload
public interface RepositoryWithUsernameAndPasswordEntity extends RepositoryEntity {

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

}
