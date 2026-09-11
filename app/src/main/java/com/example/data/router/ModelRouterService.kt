package com.example.data.router

import com.example.data.model.AiModelOption
import com.example.data.model.AiTaskCategory
import com.example.data.model.AiTaskRequest
import kotlinx.coroutines.flow.StateFlow

/**
 * Singleton gateway that exposes the pluggable [ModelRouter] implementation.
 * Allows toggling between Local/On-Device and Cloud-Based API endpoints.
 */
object ModelRouterService {

    val router: ModelRouter = DefaultModelRouter()

    val executionTarget: StateFlow<ModelExecutionTarget>
        get() = router.executionTarget

    fun setExecutionTarget(target: ModelExecutionTarget) {
        router.setExecutionTarget(target)
    }

    fun getAvailableModelsFor(category: AiTaskCategory): List<AiModelOption> {
        return router.getAvailableModelsFor(category)
    }

    suspend fun executeTask(
        request: AiTaskRequest,
        onProgress: (Float, String) -> Unit
    ): RouterExecutionResult {
        return router.executeTask(request, onProgress)
    }
}
