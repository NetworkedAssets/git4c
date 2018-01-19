package com.networkedassets.git4c.infrastructure.database.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Table;
import org.jetbrains.annotations.Nullable;

@Table("MacroSettings")
@Preload
public interface MacroSettingsEntity extends Entity {

    String getUuid();

    void setUuid(String uuid);

    String getBranch();

    void setBranch(String branch);

    String getRepository();

    void setRepository(String repository);

    String getDefaultDocItem();

    void setDefaultDocItem(String defaultDocItem);

    String getExtractor();

    void setExtractor(String extractor);

    String getRootDirectory();

    void setRootDirectory(String rootDirectory);

    String getType();

    void setType(String type);
}
