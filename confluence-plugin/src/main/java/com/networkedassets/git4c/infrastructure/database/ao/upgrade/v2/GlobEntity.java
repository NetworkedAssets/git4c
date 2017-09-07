package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v2;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

@Table("Glob")
public interface GlobEntity extends Entity {

    DocumentationsMacroSettingsEntity getMacroSettings();

    void setMacroSettings(DocumentationsMacroSettingsEntity macroSettings);

    String getGlob();

    void setGlob(String string);
}
