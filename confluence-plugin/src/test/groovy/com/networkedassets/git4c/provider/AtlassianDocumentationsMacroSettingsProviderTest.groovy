package com.networkedassets.git4c.provider

import com.networkedassets.git4c.core.bussiness.DataStore
import com.networkedassets.git4c.core.datastore.EncryptedMacroSettingsCache
import com.networkedassets.git4c.core.datastore.EncryptedMacroSettingsRepository
import com.networkedassets.git4c.data.macro.EncryptedDocumentationsMacroSettings
import com.networkedassets.git4c.data.macro.DocumentationsMacroSettings
import com.networkedassets.git4c.data.macro.NoAuthCredentials
import com.networkedassets.git4c.infrastructure.UnifiedDataStore
import com.networkedassets.git4c.infrastructure.MacroSettingsCacheEncryptor
import com.networkedassets.git4c.infrastructure.MacroSettingsDatabaseEncryptor
import com.networkedassets.git4c.infrastructure.SimpleCredentialsEncryptor
import org.jetbrains.annotations.NotNull
import spock.lang.Specification

class AtlassianDocumentationsMacroSettingsProviderTest extends Specification {
    class Store implements DataStore<EncryptedDocumentationsMacroSettings>, EncryptedMacroSettingsRepository, EncryptedMacroSettingsCache {
        def database = new HashMap<String, EncryptedDocumentationsMacroSettings>()

        @Override
        boolean isAvailable(@NotNull String id) {
            return database.containsKey(id)
        }

        @Override
        EncryptedDocumentationsMacroSettings get(@NotNull String id) {
            return database.get(id)
        }

        @Override
        void put(@NotNull String id, EncryptedDocumentationsMacroSettings data) {
            database.put(id, data)
        }

        @Override
        void remove(@NotNull String id) {
            database.remove(id)
        }

        @Override
        void removeAll() {
            database.clear()
        }
    }


    def "should provide macroSettingsFromCache"() {
        given:
        def cache = new MacroSettingsCacheEncryptor(new Store(), new SimpleCredentialsEncryptor())
        def repository = new MacroSettingsDatabaseEncryptor(new Store(), new SimpleCredentialsEncryptor())
        def provider = new UnifiedDataStore(repository, cache)
        def macroSettings1 = new DocumentationsMacroSettings("1","",new NoAuthCredentials(), "", "","")
        def macroSettings2 = new DocumentationsMacroSettings("2","",new NoAuthCredentials(),"","","")
        def macroSettings3 = new DocumentationsMacroSettings("3","",new NoAuthCredentials(),"","","")
        when:
        cache.put("1",macroSettings1)
        repository.put("1",macroSettings2)
        repository.put("2",macroSettings2)

        then:
        provider.isAvailable("1")
        provider.isAvailable("2")
        !provider.isAvailable("3")

        provider.get("1") == macroSettings1
        provider.get("2") == macroSettings2
        provider.get("3") == null

    }


}
