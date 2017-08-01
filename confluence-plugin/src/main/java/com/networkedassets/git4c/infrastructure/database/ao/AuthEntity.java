package com.networkedassets.git4c.infrastructure.database.ao;

import net.java.ao.Entity;
import net.java.ao.OneToOne;
import net.java.ao.Polymorphic;
import net.java.ao.Preload;

@Polymorphic
@Preload
public interface AuthEntity extends Entity {

    @OneToOne
    DocumentationsMacroSettingsEntity getMacroSettings();

}
