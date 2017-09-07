package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v8;

import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Table("NoAuth")
@Preload
public interface RepositoryWithNoAuthorizationEntity extends RepositoryEntity {

}
