package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.before;

import net.java.ao.Entity;
import net.java.ao.OneToOne;
import net.java.ao.Polymorphic;
import net.java.ao.Preload;

@Polymorphic
@Preload
public interface AuthEntityBefore extends Entity {

    @OneToOne
    DocumentationsMacroSettingsBefore getMacroSettings();

}
