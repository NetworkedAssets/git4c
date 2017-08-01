package com.networkedassets.git4c.Security

import com.networkedassets.git4c.core.bussiness.DataStore
import com.networkedassets.git4c.core.datastore.EncryptedMacroSettingsRepository
import com.networkedassets.git4c.data.macro.EncryptedDocumentationsMacroSettings
import com.networkedassets.git4c.data.macro.DocumentationsMacroSettings
import com.networkedassets.git4c.data.macro.UsernameAndPasswordCredentials
import com.networkedassets.git4c.infrastructure.MacroSettingsEncryptor
import com.networkedassets.git4c.infrastructure.SimpleCredentialsEncryptor
import org.jetbrains.annotations.NotNull
import spock.lang.Specification


class EncryptedDocumentationsMacroSettingsRepositoryTest extends Specification {

    class DataBase implements DataStore<EncryptedDocumentationsMacroSettings>, EncryptedMacroSettingsRepository {
        HashMap<String, EncryptedDocumentationsMacroSettings> store = new HashMap<>()
        @Override
        boolean isAvailable(@NotNull String id) {
            return store.containsKey(id)
        }

        @Override
        EncryptedDocumentationsMacroSettings get(@NotNull String id) {
            return (EncryptedDocumentationsMacroSettings)store.get(id)
        }

        @Override
        void put(@NotNull String id, EncryptedDocumentationsMacroSettings data) {
            store.put(id, data)
        }

        @Override
        void remove(@NotNull String id) {
            store.remove(id)
        }

        @Override
        void removeAll() {
            store.clear()
        }
    }




    def "should save encrypted settings"() {
        given:
        def credentials = new UsernameAndPasswordCredentials("user", "pass123")
        def settings = new DocumentationsMacroSettings("1","Path", credentials, "master" , "glob", "")

        def dataStore = new DataBase()
        def repo = new MacroSettingsEncryptor(dataStore, new SimpleCredentialsEncryptor()) {}

        when:
        repo.put("1",settings)
        def decrypted = repo.get("1")

        then:
        repo.isAvailable("1")
        dataStore.get("1").credentials!= decrypted.credentials
        decrypted.credentials == settings.credentials

    }
}
