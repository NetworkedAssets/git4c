package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.after;

import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Table("NoAuth")
@Preload
public interface NoAuthEntityAfter extends AuthEntityAfter {

}
