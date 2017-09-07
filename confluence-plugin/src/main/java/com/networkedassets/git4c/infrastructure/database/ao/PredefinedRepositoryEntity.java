package com.networkedassets.git4c.infrastructure.database.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Table("PredefinedRepo")
@Preload
public interface PredefinedRepositoryEntity extends Entity {

    String getUuid();

    void setUuid(String uuid);

    String getRepository();

    void setRepository(String repository);

    String getName();

    void setName(String name);
}