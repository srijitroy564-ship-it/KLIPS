package com.example.data.router

import com.example.data.model.AiModelOption
import com.example.data.model.AiTaskCategory
import com.example.data.model.AiTaskRequest
import com.example.data.model.CaptionItem
import com.example.data.model.TimelineClip
import kotlinx.coroutines.flow.StateFlow

/**
 * Execution target mode for AI models:
 * Local on-device NPU/Edge vs Cloud API endpoints vs Auto Hybrid routing.
 */
enum class ModelExecutionTarget(val label: String, val badge: String, val description: String) {
    LOCAL_ON_DEVICE("On-Device (NPU/Edge)", "⚡ Local", "Zero latency, private, runs on local device hardware"),
    CLOUD_API("Cloud AI Endpoints", "☁️ Cloud", "Max quality, multi-modal cloud GPU cluster (Gemini, Veo, Flux)"),
    AUTO_HYBRID("Smart Hybrid Router", "🔄 Hybrid", "Auto-selects optimal target based on battery, network & model tier")
}

data class RouterExecutionResult(
    val message: String,
    val details: String? = null,
    val clips: List<TimelineClip> = emptyList(),
    val captions: List<CaptionItem> = emptyList(),
    val modifiedFilter: String? = null,
    val smartCutApplied: Boolean = false,
    val creditsDeducted: Int = 0,
    val modelTargetUsed: ModelExecutionTarget = ModelExecutionTarget.CLOUD_API,
    val latencyMs: Long = 850L
)

/**
 * Pluggable Model Router interface to toggle between Local/On-Device models
 * and Cloud-Based API endpoints, with dedicated modular sub-services.
 */
interface ModelRouter {
    val executionTarget: StateFlow<ModelExecutionTarget>
    fun setExecutionTarget(target: ModelExecutionTarget)

    val imageService: ImageGenerationService
    val videoService: VideoGenerationService
    val captioningService: CaptioningService
    val smartEditingService: SmartEditingService
    val audioProcessingService: AudioProcessingService
    val visualFxService: VisualFxService
    val workflowService: WorkflowService

    fun getAvailableModelsFor(category: AiTaskCategory): List<AiModelOption>
    suspend fun executeTask(
        request: AiTaskRequest,
        onProgress: (Float, String) -> Unit
    ): RouterExecutionResult
}

/**
 * Mock service for Image Generation (Imagen 3, Flux.1 via Hugging Face Open API).
 */
interface ImageGenerationService {
    suspend fun generateImage(
        prompt: String,
        modelId: String,
        target: ModelExecutionTarget
    ): ImageResult
}

data class ImageResult(
    val title: String,
    val drawableResId: Int,
    val provider: String,
    val prompt: String
)

/**
 * Mock service for Video Generation (Veo 3.1, Imagen Video, Flow Generative Video).
 */
interface VideoGenerationService {
    suspend fun generateVideo(
        prompt: String,
        durationMs: Long,
        modelId: String,
        target: ModelExecutionTarget
    ): VideoResult
}

data class VideoResult(
    val clip: TimelineClip,
    val resolution: String,
    val fps: Int
)

/**
 * Mock service for Captioning & Subtitles (Whisper AI, Gemini 1.5 Flash STT, On-Device STT).
 */
interface CaptioningService {
    suspend fun generateCaptions(
        audioClipId: String?,
        modelId: String,
        target: ModelExecutionTarget
    ): List<CaptionItem>
}

/**
 * Mock service for AI-Powered Smart Editing:
 * - Text-based editing (Descript style)
 * - Auto Silence Cut
 * - Auto Highlight Reel
 * - Smart Auto-Reframe & 1-Click Viral Resizer
 * - AI Beat Sync & Speed Ramping
 */
interface SmartEditingService {
    suspend fun applySilenceCut(clips: List<TimelineClip>): Pair<List<TimelineClip>, Long>
    suspend fun applyTextBasedEdit(removedWordOffsets: List<LongRange>, clip: TimelineClip): TimelineClip
    suspend fun generateHighlightReel(clips: List<TimelineClip>): List<TimelineClip>
    suspend fun applyAutoReframe(targetAspectLabel: String): String
    suspend fun syncToBeats(audioTrackId: String, clips: List<TimelineClip>): List<TimelineClip>
}

/**
 * Mock service for Voice & Audio:
 * - AI Voice Cleaner & Studio Sound (acoustic crystal-clear enhancement)
 * - Voice Cloning TTS
 * - AI Dubbing (multi-language lip sync)
 * - AI Background Noise Removal
 * - AI Music Generation
 */
interface AudioProcessingService {
    suspend fun enhanceVoiceStudioSound(clipId: String): String
    suspend fun cloneVoiceAndSynthesize(script: String, voiceName: String): TimelineClip
    suspend fun dubVideo(targetLang: String, originalClipId: String): TimelineClip
    suspend fun generateAiMusic(genre: String, bpm: Int, durationMs: Long): TimelineClip
}

/**
 * Mock service for Visual / Tracking FX:
 * - 3D Camera & Motion Tracking
 * - Green Screen without Screen (AI Background Remover + Flux background)
 * - AI Movie Color Grade & AI Color Match
 * - AI Face & Body Re-lighting (Cyberpunk Neon, Golden Hour, Studio Softbox)
 * - AI Kinetic Text & Motion Graphics (Marvel, Fire Dust, 3D Glass)
 * - AI Film Artifacts & Grain (16mm, 35mm, Light Leaks, VCR Glitch)
 */
interface VisualFxService {
    suspend fun apply3DCameraTracking(targetClipId: String, trackedObject: String): String
    suspend fun removeBackgroundAndReplace(targetClipId: String, backgroundPrompt: String): Pair<String, TimelineClip?>
    suspend fun applyMovieColorGrade(paletteName: String): String
    suspend fun applyFaceRelighting(lightPreset: String): String
    suspend fun applyKineticTextFx(textStyle: String): CaptionItem
    suspend fun applyFilmGrainAndArtifacts(filmStock: String): String
}

/**
 * Mock service for Workflow & Creator Kits:
 * - Cloud Sync + Multi-Device Projects
 * - Creator Branding Kit (Intro/Outro/Logo Bumper Kit)
 * - AI Script / Storyboard Generator
 */
interface WorkflowService {
    suspend fun generateScriptAndStoryboard(topic: String): StoryboardData
    suspend fun generateBrandingBumperKit(creatorName: String): Pair<TimelineClip, TimelineClip>
}

data class StoryboardScene(
    val sceneNumber: Int,
    val title: String,
    val shotType: String,
    val visualPrompt: String,
    val narration: String,
    val durationSec: Int
)

data class StoryboardData(
    val topic: String,
    val logline: String,
    val hook: String,
    val scenes: List<StoryboardScene>
)
