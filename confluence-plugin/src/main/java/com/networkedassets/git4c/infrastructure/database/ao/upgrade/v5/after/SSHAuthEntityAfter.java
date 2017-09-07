package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.after;

import net.java.ao.Preload;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Table("SSHAuth")
@Preload
public interface SSHAuthEntityAfter extends AuthEntityAfter {

    @StringLength(value = StringLength.UNLIMITED)
    String getKey();

    @StringLength(value = StringLength.UNLIMITED)
    void setKey(String key);
}
