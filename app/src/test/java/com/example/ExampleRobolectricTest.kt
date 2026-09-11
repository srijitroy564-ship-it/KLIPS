package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AiTaskCategory
import com.example.data.router.ModelRouterService
import com.example.data.router.SampleProjects
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Klipz", appName)
    }

    @Test
    fun `model router provides available models`() {
        val models = ModelRouterService.getAvailableModelsFor(AiTaskCategory.AUTO_CAPTIONS)
        assertTrue(models.isNotEmpty())
        assertTrue(models.any { it.id == "gemini-3.5-flash" })
    }

    @Test
    fun `credits manager deducts and awards coins properly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val creditsManager = com.example.data.auth.CreditsManager(context)
        creditsManager.resetToDefault()
        assertEquals(50, creditsManager.coins.value)

        val deducted = creditsManager.deductCoins(5, "Test AI Task")
        assertTrue(deducted)
        assertEquals(45, creditsManager.coins.value)

        creditsManager.addCoins(10, "Bonus Purchase")
        assertEquals(55, creditsManager.coins.value)
    }

    @Test
    fun `model router switches execution target`() {
        com.example.data.router.ModelRouterService.setExecutionTarget(com.example.data.router.ModelExecutionTarget.LOCAL_ON_DEVICE)
        assertEquals(com.example.data.router.ModelExecutionTarget.LOCAL_ON_DEVICE, com.example.data.router.ModelRouterService.executionTarget.value)

        com.example.data.router.ModelRouterService.setExecutionTarget(com.example.data.router.ModelExecutionTarget.CLOUD_API)
        assertEquals(com.example.data.router.ModelExecutionTarget.CLOUD_API, com.example.data.router.ModelRouterService.executionTarget.value)
    }
}
