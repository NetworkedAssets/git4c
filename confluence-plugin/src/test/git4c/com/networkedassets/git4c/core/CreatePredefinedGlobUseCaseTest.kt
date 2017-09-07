package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.CreatePredefinedGlobCommand
import com.networkedassets.git4c.boundary.inbound.PredefinedGlobToCreate
import com.networkedassets.git4c.test.UseCaseTest
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CreatePredefinedGlobUseCaseTest : UseCaseTest<CreatePredefinedGlobUseCase>() {

    override fun getUseCase(plugin: PluginComponents): CreatePredefinedGlobUseCase {
        return CreatePredefinedGlobUseCase(plugin.predefinedGlobsDatabase, plugin.idGenerator)
    }

    @Test
    fun `Predefined Glob should be created`() {
        val globToCreate = PredefinedGlobToCreate("glob", "**.glob")

        val answer = useCase.execute(CreatePredefinedGlobCommand(globToCreate))

        assertNull(answer.component2())
        assertNotNull(answer.component1())
        assertTrue(components.predefinedGlobsDatabase.get(answer.get().uuid)!!.name.equals(globToCreate.name))
        assertTrue(components.predefinedGlobsDatabase.get(answer.get().uuid)!!.glob.equals(globToCreate.glob))
    }
}