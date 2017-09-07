package com.networkedassets.git4c.infrastructure.database.ao.upgrade.v4;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

@Table("Glob")
public interface GlobEntityBefore extends Entity {

    DocumentationsMacroSettingsBefore getMacroSettings();

    void setMacroSettings(DocumentationsMacroSettingsBefore macroSettings);

    String getGlob();

    void setGlob(String string);
}
