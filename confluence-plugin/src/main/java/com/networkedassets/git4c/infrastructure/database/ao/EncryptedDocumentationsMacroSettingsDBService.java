package com.networkedassets.git4c.infrastructure.database.ao;

import com.atlassian.activeobjects.tx.Transactional;
import com.networkedassets.git4c.data.macro.EncryptedDocumentationsMacroSettings;
import org.jetbrains.annotations.Nullable;

@Transactional
public interface EncryptedDocumentationsMacroSettingsDBService {

    boolean isAvailable(String uuid);

    @Nullable
    EncryptedDocumentationsMacroSettings getSettings(String uuid);

    void add(EncryptedDocumentationsMacroSettings documentationsMacroSettings);

    void remove(String uuid);

    void removeAll();
}
