package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v10;

import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Table("NoAuth")
@Preload
public interface RepositoryWithNoAuthorizationEntityBefore extends RepositoryEntityBefore {

}
