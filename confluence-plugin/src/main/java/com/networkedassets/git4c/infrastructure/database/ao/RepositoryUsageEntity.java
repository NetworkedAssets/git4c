package com.networkedassets.git4c.infrastructure.database.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Table("RepositoryUsages")
@Preload
public interface RepositoryUsageEntity extends Entity {

    String getUuid();
    void setUuid(String uuid);

    String getUsername();
    void setUsername(String name);

    String getRepository();
    void setRepository(String repositoryUuid);

    String getRepositoryName();
    void setRepositoryName(String repositoryUuid);

    Long getDate();
    void setDate(Long date);
}
