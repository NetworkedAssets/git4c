package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v10;

import net.java.ao.Preload;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Table("SSHAuth")
@Preload
public interface RepositoryWithSshKeyEntityAfter extends RepositoryEntityAfter {

    @StringLength(value = StringLength.UNLIMITED)
    String getKey();

    @StringLength(value = StringLength.UNLIMITED)
    void setKey(String key);
}
