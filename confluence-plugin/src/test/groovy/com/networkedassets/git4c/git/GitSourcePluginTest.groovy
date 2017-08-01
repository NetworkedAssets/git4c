package com.networkedassets.git4c.git

import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.infrastructure.plugin.source.git.GitSourcePlugin
import org.junit.experimental.categories.Category
import spock.lang.IgnoreIf
import spock.lang.Specification

@Category(IntegrationTest)
class GitSourcePluginTest extends Specification {
/*

    def client = new DefaultGitClient()
    def plugin = new GitSourcePlugin(client)
    def password = System.getProperty("pwd")
    def username = System.getProperty("usr")
    def sourceDataIdentifier = System.getProperty("sourceDataIdentifier")

    @IgnoreIf({
        System.getProperty("pwd")?.size() <= 0 ||
                System.getProperty("usr")?.size() <= 0 ||
                System.getProperty("sourceDataIdentifier")?.size() <= 0
    })
    def "Should return sourceData for sourceData without an exception"() {

        given:
        def usernameField = new SourceSettingField("username", "username", username, SourceSettingField.Type.TEXT)
        def passwordField = new SourceSettingField("password", "password", password, SourceSettingField.Type.PASSWORD)
        def settings = [username: usernameField, password: passwordField]
        def sourceData = new SourceData(new SourceUnitIdentifier(sourceDataIdentifier), settings)

        when:
        VerificationInfo verificationInfo = plugin.verify(sourceData)

        then:
        verificationInfo.getStatus().equals(SourcePlugin.VerificationStatus.OK)
    }


    @IgnoreIf({
        System.getProperty("pwd")?.size() <= 0 ||
                System.getProperty("usr")?.size() <= 0 ||
                System.getProperty("sourceDataIdentifier")?.size() <= 0
    })
    def "Should return correct rawData"() {

        given:
        def usernameField = new SourceSettingField("username", "username", username, SourceSettingField.Type.TEXT)
        def passwordField = new SourceSettingField("password", "password", password, SourceSettingField.Type.PASSWORD)
        def settings = [username: usernameField, password: passwordField]
        def sourceData = new SourceData(new SourceUnitIdentifier(sourceDataIdentifier), settings)

        when:
        def rawData = plugin.fetchRawData(sourceData)

        then:
        rawData.isPresent()

    }

    def "Should return status SOURCE_NOT_FOUND if url is not correct for Bitbucket"() {

        given:

        def usernameField = new SourceSettingField("username", "username", "admin", SourceSettingField.Type.TEXT)
        def passwordField = new SourceSettingField("password", "password", "admin", SourceSettingField.Type.PASSWORD)
        def settings = [username: usernameField, password: passwordField]
        def sourceData = new SourceData(new SourceUnitIdentifier("http://atlasdemo.networkedassets.net/bitbucket2/scm/doc/doc.git"), settings)

        when:

        VerificationInfo verificationInfo = plugin.verify(sourceData)
        then:

        verificationInfo.getStatus().equals(SourcePlugin.VerificationStatus.SOURCE_NOT_FOUND)
    }


    def "Should return status WRONG_CREDENTIALS if credentials are incorrect for GitHub"() {

        given:

        def usernameField = new SourceSettingField("username", "username", "admin", SourceSettingField.Type.TEXT)
        def passwordField = new SourceSettingField("password", "password", "admin", SourceSettingField.Type.PASSWORD)
        def settings = [username: usernameField, password: passwordField]
        def sourceData = new SourceData(new SourceUnitIdentifier("https://github.com/NetworkedAssets/condoc.git"), settings)


        when:
        VerificationInfo verificationInfo = plugin.verify(sourceData)

        then:
        verificationInfo.getStatus().equals(SourcePlugin.VerificationStatus.WRONG_CREDENTIALS)
    }


    def "Should return status SOURCE_NOT_FOUND if url is not correct for GitHub"() {

        given:

        def usernameField = new SourceSettingField("username", "username", "admin", SourceSettingField.Type.TEXT)
        def passwordField = new SourceSettingField("password", "password", "admin", SourceSettingField.Type.PASSWORD)
        def settings = [username: usernameField, password: passwordField]
        def sourceData = new SourceData(new SourceUnitIdentifier("https://github2.com/NetworkedAssets/condoc.git"), settings)

        when:
        VerificationInfo verificationInfo = plugin.verify(sourceData)

        then:
        verificationInfo.getStatus().equals(SourcePlugin.VerificationStatus.SOURCE_NOT_FOUND)
    }

*/

}
