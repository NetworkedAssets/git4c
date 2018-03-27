package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.GetRepositoryUsagesForUserQuery
import com.networkedassets.git4c.data.RepositoryUsage
import com.networkedassets.git4c.test.UseCaseTest
import org.junit.Test
import java.util.*
import kotlin.test.assertTrue

class GetRepositoryUsagesForUserUseCaseTest : UseCaseTest<GetRepositoryUsagesForUserUseCase>() {

    override fun getUseCase(plugin: PluginComponents): GetRepositoryUsagesForUserUseCase {
        return GetRepositoryUsagesForUserUseCase(plugin.bussines)
    }

    @Test
    fun `Should return empty list when no repositories used recently`() {
        val user = "anonymous"
        val query = GetRepositoryUsagesForUserQuery(user)

        val answer = useCase.execute(query)

        assertTrue { answer.component1()!!.usages.isEmpty() }
        assertTrue ( answer.component2() == null  )

    }

    @Test
    fun `Should return non empty list when repositories used recently`() {
        val user = "anonymous"
        val query = GetRepositoryUsagesForUserQuery(user)

        useCase.repositoryUsageDatabase.put("0", RepositoryUsage("0", user, "repo1ID", "repo1", Date().time))
        useCase.repositoryUsageDatabase.put("1", RepositoryUsage("1", user, "repo2ID", "repo2", Date().time))

        val answer = useCase.execute(query)

        assertTrue { answer.component1()!!.usages.isNotEmpty() }
        assertTrue ( answer.component2() == null  )

    }
}