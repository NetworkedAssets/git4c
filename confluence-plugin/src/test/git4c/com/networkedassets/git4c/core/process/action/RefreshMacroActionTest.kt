package com.networkedassets.git4c.core.process.action

import com.jayway.awaitility.Awaitility.await
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.utils.genTransactionId
import com.networkedassets.git4c.utils.InMemoryApplication.getComponents
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.concurrent.TimeUnit

class RefreshMacroActionTest {

    val component = getComponents()
    val action = component.refreshProcess

    @Test
    fun `Fetch and convert from repository should be done in short time`() {

        // Setup
        val list = mutableListOf<String>()
        val times = mutableListOf<Long>()

        // Given
        val numberOfRefreshes = 100

        // When
        for (i in 1..numberOfRefreshes) {
            Thread {
                val repository = RepositoryWithNoAuthorization(genTransactionId(), "src/test/resources")
                val settings = MacroSettings(genTransactionId(), repository.uuid, "master", "item", null, null)
                action.macroSettingsDatabase.put(settings.uuid, settings)
                action.repositoryDatabase.put(repository.uuid, repository)
                val startTime = System.currentTimeMillis()
                action.fetchDataFromSourceThenConvertAndCache(
                        settings.uuid,
                        repository.repositoryPath,
                        settings.branch,
                        Runnable {
                            list.add(settings.uuid)
                            val time = System.currentTimeMillis() - startTime
                            times.add(time)
                        },
                        Runnable {}
                )
            }.run()
        }

        // Then
        await().atMost(30, TimeUnit.SECONDS).until {
            assertThat(list.size).isEqualTo(numberOfRefreshes)
            assertThat(times.size).isEqualTo(numberOfRefreshes)
        }
    }
}
