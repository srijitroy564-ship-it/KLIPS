package com.example.data.router

import com.example.data.model.AiModelOption
import com.example.data.model.AiTaskCategory
import com.example.data.model.AiTaskRequest
import com.example.data.model.TimelineClip
import com.example.data.router.services.MockAudioProcessingService
import com.example.data.router.services.MockCaptioningService
import com.example.data.router.services.MockImageGenerationService
import com.example.data.router.services.MockSmartEditingService
import com.example.data.router.services.MockVideoGenerationService
import com.example.data.router.services.MockVisualFxService
import com.example.data.router.services.MockWorkflowService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class DefaultModelRouter : ModelRouter {

    private val _executionTarget = MutableStateFlow(ModelExecutionTarget.AUTO_HYBRID)
    override val executionTarget: StateFlow<ModelExecutionTarget> = _executionTarget.asStateFlow()

    override val imageService: ImageGenerationService = MockImageGenerationService()
    override val videoService: VideoGenerationService = MockVideoGenerationService()
    override val captioningService: CaptioningService = MockCaptioningService()
    override val smartEditingService: SmartEditingService = MockSmartEditingService()
    override val audioProcessingService: AudioProcessingService = MockAudioProcessingService()
    override val visualFxService: VisualFxService = MockVisualFxService()
    override val workflowService: WorkflowService = MockWorkflowService()

    override fun setExecutionTarget(target: ModelExecutionTarget) {
        _executionTarget.value = target
    }

    override fun getAvailableModelsFor(category: AiTaskCategory): List<AiModelOption> {
        val current = _executionTarget.value
        val all = when (category) {
            AiTaskCategory.AUTO_CAPTIONS -> listOf(
                AiModelOption(
                    id = "whisper-large-v3",
                    name = "Whisper AI v3 (Open-source)",
                    provider = "OpenAI / HuggingFace",
                    tier = "1 Credit",
                    latencyEstimate = "~2.2s",
                    isRecommended = true
                ),
                AiModelOption(
                    id = "gemini-1.5-flash-stt",
                    name = "Gemini 1.5 Flash STT",
                    provider = "Google AI Studio",
                    tier = "Free / High Speed",
                    latencyEstimate = "~1.1s"
                ),
                AiModelOption(
                    id = "ondevice-whisper-tiny",
                    name = "On-Device Neural STT",
                    provider = "Local Hardware NPU",
                    tier = "Free / Offline",
                    latencyEstimate = "~0.3s",
                    isLocalOnDevice = true
                )
            )

            AiTaskCategory.TEXT_BASED_EDITING -> listOf(
                AiModelOption(
                    id = "descript-nlp-sync",
                    name = "Descript-Style Transcript Align",
                    provider = "Klipz Smart Engine",
                    tier = "Free",
                    latencyEstimate = "~0.4s",
                    isRecommended = true,
                    isLocalOnDevice = true
                ),
                AiModelOption(
                    id = "gemini-transcript-opt",
                    name = "Gemini 1.5 Pro Semantic Cut",
                    provider = "Google AI Studio",
                    tier = "1 Credit",
                    latencyEstimate = "~1.8s"
                )
            )

            AiTaskCategory.SMART_CUT -> listOf(
                AiModelOption(
                    id = "local-silence-detector",
                    name = "Edge Silence & Breath Remover",
                    provider = "Local DSP Core",
                    tier = "Free / Offline",
                    latencyEstimate = "~0.2s",
                    isRecommended = true,
                    isLocalOnDevice = true
                ),
                AiModelOption(
                    id = "cloud-nlp-silence",
                    name = "Cloud Rhythm & Pause Cut",
                    provider = "Klipz Cloud Audio",
                    tier = "1 Credit",
                    latencyEstimate = "~1.5s"
                )
            )

            AiTaskCategory.AUTO_HIGHLIGHT_REEL -> listOf(
                AiModelOption(
                    id = "gemini-highlight-curator",
                    name = "Gemini 1.5 Flash Video Curator",
                    provider = "Google AI Studio",
                    tier = "2 Credits",
                    latencyEstimate = "~3.2s",
                    isRecommended = true
                ),
                AiModelOption(
                    id = "local-energy-detector",
                    name = "Audio/Motion Peak Selector",
                    provider = "Local Hardware Core",
                    tier = "Free / Offline",
                    latencyEstimate = "~0.6s",
                    isLocalOnDevice = true
                )
            )

            AiTaskCategory.SMART_AUTO_REFRAME -> listOf(
                AiModelOption(
                    id = "edge-yolo-face-reframe",
                    name = "Smart Subject Tracker (9:16)",
                    provider = "Local Tensor RT",
                    tier = "Free / Offline",
                    latencyEstimate = "~0.4s",
                    isRecommended = true,
                    isLocalOnDevice = true
                ),
                AiModelOption(
                    id = "cloud-multi-object-reframe",
                    name = "Cloud Cinemagraphic Panning",
                    provider = "Klipz Cloud Vision",
                    tier = "1 Credit",
                    latencyEstimate = "~1.9s"
                )
            )

            AiTaskCategory.GENERATE_BROLL -> listOf(
                AiModelOption(
                    id = "veo-3.1-cinematic",
                    name = "Veo 3.1 Cinematic 4K",
                    provider = "Google DeepMind",
                    tier = "3 Credits",
                    latencyEstimate = "~5.5s",
                    isRecommended = true
                ),
                AiModelOption(
                    id = "veo-3.1-fast",
                    name = "Veo 3.1 Fast B-Roll",
                    provider = "Google DeepMind",
                    tier = "2 Credits",
                    latencyEstimate = "~3.8s"
                ),
                AiModelOption(
                    id = "flow-generative-video",
                    name = "Flow Generative Video",
                    provider = "Flow Open Cloud",
                    tier = "2 Credits",
                    latencyEstimate = "~4.2s"
                )
            )

            AiTaskCategory.FLUX_IMAGEN_IMAGE -> listOf(
                AiModelOption(
                    id = "flux-1-schnell",
                    name = "Flux.1 Schnell (High-Res)",
                    provider = "Hugging Face Open API",
                    tier = "Free / 1 Credit",
                    latencyEstimate = "~2.8s",
                    isRecommended = true
                ),
                AiModelOption(
                    id = "imagen-3-free-tier",
                    name = "Google Imagen 3 (Free Tier)",
                    provider = "Google AI Studio",
                    tier = "Free Tier",
                    latencyEstimate = "~3.1s"
                ),
                AiModelOption(
                    id = "stable-diffusion-turbo-edge",
                    name = "On-Device Mobile Diffusion",
                    provider = "Local NPU Accelerator",
                    tier = "Free / Offline",
                    latencyEstimate = "~1.2s",
                    isLocalOnDevice = true
                )
            )

            AiTaskCategory.VOICE_CLEANER_STUDIO -> listOf(
                AiModelOption(
                    id = "deep-filter-acoustic",
                    name = "Klipz Studio Sound 3.0",
                    provider = "Acoustic Neural DSP",
                    tier = "Free",
                    latencyEstimate = "~0.9s",
                    isRecommended = true,
                    isLocalOnDevice = true
                ),
                AiModelOption(
                    id = "adobe-enhance-cloud",
                    name = "Cloud Studio Multi-Band Clear",
                    provider = "Cloud Audio Pro",
                    tier = "1 Credit",
                    latencyEstimate = "~2.4s"
                )
            )

            AiTaskCategory.VOICE_CLONING -> listOf(
                AiModelOption(
                    id = "elevenlabs-voice-clone",
                    name = "Ultra-Realistic Voice Clone",
                    provider = "ElevenLabs Neural",
                    tier = "2 Credits",
                    latencyEstimate = "~2.5s",
                    isRecommended = true
                ),
                AiModelOption(
                    id = "gemini-tts-natural",
                    name = "Gemini TTS (Warm Narrator)",
                    provider = "Google AI Studio",
                    tier = "Free",
                    latencyEstimate = "~1.0s"
                )
            )

            AiTaskCategory.AI_DUBBING -> listOf(
                AiModelOption(
                    id = "wav2lip-sync-dub",
                    name = "AI Lip-Sync Multi-Dub",
                    provider = "Cloud Translation Matrix",
                    tier = "3 Credits",
                    latencyEstimate = "~4.5s",
                    isRecommended = true
                )
            )

            AiTaskCategory.AI_MUSIC_GEN -> listOf(
                AiModelOption(
                    id = "musiclm-synth-beat",
                    name = "AI Custom Soundtrack Generator",
                    provider = "Google Cloud Audio",
                    tier = "1 Credit",
                    latencyEstimate = "~3.0s",
                    isRecommended = true
                ),
                AiModelOption(
                    id = "local-beat-generator",
                    name = "On-Device Lo-Fi Beat Maker",
                    provider = "Local MIDI Synthesizer",
                    tier = "Free / Offline",
                    latencyEstimate = "~0.2s",
                    isLocalOnDevice = true
                )
            )

            AiTaskCategory.CAMERA_MOTION_TRACKING -> listOf(
                AiModelOption(
                    id = "opencv-3d-cam-track",
                    name = "Interactive 3D Camera Tracking",
                    provider = "Edge AR Spatial Core",
                    tier = "Free / Offline",
                    latencyEstimate = "~0.7s",
                    isRecommended = true,
                    isLocalOnDevice = true
                ),
                AiModelOption(
                    id = "cloud-nerf-cam-track",
                    name = "6-DoF Neural Gaussian Anchor",
                    provider = "Cloud Spatial AI",
                    tier = "2 Credits",
                    latencyEstimate = "~3.5s"
                )
            )

            AiTaskCategory.REMOVE_BACKGROUND -> listOf(
                AiModelOption(
                    id = "birefnet-edge-matting",
                    name = "Green Screen without Screen (Alpha)",
                    provider = "Edge Segmentation Tensor",
                    tier = "Free / Offline",
                    latencyEstimate = "~1.1s",
                    isRecommended = true,
                    isLocalOnDevice = true
                ),
                AiModelOption(
                    id = "flow-cloud-backdrop-replace",
                    name = "Flow AI Generated Background",
                    provider = "Flow Open Cloud",
                    tier = "2 Credits",
                    latencyEstimate = "~3.9s"
                )
            )

            AiTaskCategory.MOVIE_COLOR_GRADE -> listOf(
                AiModelOption(
                    id = "cinematic-movie-palette",
                    name = "AI Movie Palette Grade (Screenshot LUT)",
                    provider = "Klipz Color Matrix",
                    tier = "Free",
                    latencyEstimate = "~0.8s",
                    isRecommended = true,
                    isLocalOnDevice = true
                ),
                AiModelOption(
                    id = "multi-clip-color-match",
                    name = "Multi-Clip Auto Color Harmonizer",
                    provider = "Cloud Color Science",
                    tier = "1 Credit",
                    latencyEstimate = "~2.0s"
                )
            )

            AiTaskCategory.FACE_BODY_RELIGHTING -> listOf(
                AiModelOption(
                    id = "portrait-relight-engine",
                    name = "Studio Neon / Golden Hour Relighting",
                    provider = "Edge Shader Light",
                    tier = "Free",
                    latencyEstimate = "~1.2s",
                    isRecommended = true,
                    isLocalOnDevice = true
                )
            )

            AiTaskCategory.KINETIC_TEXT_VFX -> listOf(
                AiModelOption(
                    id = "marvel-fire-dust-text",
                    name = "Marvel / Fire Dust / 3D Glass Kinetic Text",
                    provider = "Klipz Motion Graphics",
                    tier = "Free",
                    latencyEstimate = "~0.5s",
                    isRecommended = true,
                    isLocalOnDevice = true
                )
            )

            AiTaskCategory.FILM_GRAIN_ARTIFACTS -> listOf(
                AiModelOption(
                    id = "vintage-film-stock-grain",
                    name = "16mm/35mm Grain, Light Leaks & VCR Glitch",
                    provider = "Vintage Analog FX",
                    tier = "Free / Offline",
                    latencyEstimate = "~0.3s",
                    isRecommended = true,
                    isLocalOnDevice = true
                )
            )

            AiTaskCategory.SCRIPT_STORYBOARD_GEN -> listOf(
                AiModelOption(
                    id = "gemini-1.5-pro-scriptwriter",
                    name = "Gemini 1.5 Pro Storyboard Architect",
                    provider = "Google AI Studio",
                    tier = "Free / High Quality",
                    latencyEstimate = "~1.9s",
                    isRecommended = true
                )
            )

            AiTaskCategory.BRANDING_BUMPER_KIT -> listOf(
                AiModelOption(
                    id = "creator-brand-bumper-kit",
                    name = "Custom Intro/Outro/Logo Bumper Kit",
                    provider = "Klipz Branding Studio",
                    tier = "Free",
                    latencyEstimate = "~0.6s",
                    isRecommended = true,
                    isLocalOnDevice = true
                )
            )
        }

        return when (current) {
            ModelExecutionTarget.LOCAL_ON_DEVICE -> all.sortedByDescending { it.isLocalOnDevice }
            ModelExecutionTarget.CLOUD_API -> all.sortedBy { it.isLocalOnDevice }
            ModelExecutionTarget.AUTO_HYBRID -> all
        }
    }

    override suspend fun executeTask(
        request: AiTaskRequest,
        onProgress: (Float, String) -> Unit
    ): RouterExecutionResult = withContext(Dispatchers.IO) {
        val target = _executionTarget.value
        val model = getAvailableModelsFor(request.taskCategory).find { it.id == request.selectedModelId }
        val isLocal = model?.isLocalOnDevice == true || target == ModelExecutionTarget.LOCAL_ON_DEVICE

        val targetLabel = if (isLocal) "⚡ Local On-Device NPU" else "☁️ Cloud API Endpoints"
        onProgress(0.15f, "Routing via $targetLabel to ${model?.name ?: request.selectedModelId}...")
        delay(if (isLocal) 200 else 400)

        onProgress(0.45f, "Analyzing audio/video frames & neural embeddings...")
        delay(if (isLocal) 250 else 550)

        onProgress(0.75f, "Synthesizing neural model outputs...")
        delay(if (isLocal) 250 else 600)

        onProgress(0.95f, "Assembling non-destructive timeline output...")
        delay(150)

        when (request.taskCategory) {
            AiTaskCategory.AUTO_CAPTIONS -> {
                val captions = captioningService.generateCaptions(request.targetClipId, request.selectedModelId, target)
                RouterExecutionResult(
                    message = "Whisper/Gemini Subtitles generated! Word-by-word synced viral captions added.",
                    captions = captions,
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API,
                    creditsDeducted = if (isLocal) 0 else 1
                )
            }

            AiTaskCategory.TEXT_BASED_EDITING -> {
                RouterExecutionResult(
                    message = "Descript-Style Text Edit applied: Timeline clips automatically trimmed to match edited transcript!",
                    smartCutApplied = true,
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }

            AiTaskCategory.SMART_CUT -> {
                val (_, removedMs) = smartEditingService.applySilenceCut(emptyList())
                RouterExecutionResult(
                    message = "Auto Silence Cut completed! Detected 4 silent pauses (${removedMs / 1000f}s dead air removed).",
                    smartCutApplied = true,
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }

            AiTaskCategory.AUTO_HIGHLIGHT_REEL -> {
                val highlights = smartEditingService.generateHighlightReel(emptyList())
                RouterExecutionResult(
                    message = "Auto Highlight Reel synthesized! Best moments condensed into viral 15s short video.",
                    clips = highlights,
                    creditsDeducted = if (isLocal) 0 else 2,
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }

            AiTaskCategory.SMART_AUTO_REFRAME -> {
                val status = smartEditingService.applyAutoReframe("9:16 Viral TikTok/Reels")
                RouterExecutionResult(
                    message = status,
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }

            AiTaskCategory.GENERATE_BROLL -> {
                val res = videoService.generateVideo(
                    prompt = request.prompt,
                    durationMs = 4000L,
                    modelId = request.selectedModelId,
                    target = target
                )
                RouterExecutionResult(
                    message = "Veo 3.1 Generative B-Roll clip created (4K UHD) and inserted at playhead!",
                    clips = listOf(res.clip),
                    creditsDeducted = if (isLocal) 0 else 3,
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }

            AiTaskCategory.FLUX_IMAGEN_IMAGE -> {
                val res = imageService.generateImage(
                    prompt = request.prompt,
                    modelId = request.selectedModelId,
                    target = target
                )
                val newClip = TimelineClip(
                    id = "flux_image_${System.currentTimeMillis()}",
                    title = res.title,
                    drawableResId = res.drawableResId,
                    startTimeMs = 2000L,
                    durationMs = 5000L,
                    isAiGenerated = true,
                    filterName = "Flux Vivid Grade"
                )
                RouterExecutionResult(
                    message = "${res.provider} generated ultra-high-definition backdrop '${res.title}'!",
                    clips = listOf(newClip),
                    creditsDeducted = if (isLocal) 0 else 1,
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }

            AiTaskCategory.VOICE_CLEANER_STUDIO -> {
                val msg = audioProcessingService.enhanceVoiceStudioSound(request.targetClipId ?: "clip_audio")
                RouterExecutionResult(
                    message = msg,
                    modifiedFilter = "Acoustic Studio Mastered",
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }

            AiTaskCategory.VOICE_CLONING -> {
                val clip = audioProcessingService.cloneVoiceAndSynthesize(
                    script = request.prompt.ifBlank { "Welcome back creators, today we are taking editing to the next level." },
                    voiceName = "Custom Cloned Voice"
                )
                RouterExecutionResult(
                    message = "Voice cloned and speech synthesized with natural inflections!",
                    clips = listOf(clip),
                    creditsDeducted = if (isLocal) 0 else 2,
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }

            AiTaskCategory.AI_DUBBING -> {
                val clip = audioProcessingService.dubVideo("Spanish (LatAm)", request.targetClipId ?: "orig")
                RouterExecutionResult(
                    message = "AI Lip-Sync Dubbing complete! Translated audio synchronized with mouth movements.",
                    clips = listOf(clip),
                    creditsDeducted = if (isLocal) 0 else 3,
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }

            AiTaskCategory.AI_MUSIC_GEN -> {
                val musicClip = audioProcessingService.generateAiMusic("Synthwave Cyber", 128, 10000L)
                RouterExecutionResult(
                    message = "Royalty-free AI soundtrack generated (128 BPM) and aligned to timeline!",
                    clips = listOf(musicClip),
                    creditsDeducted = if (isLocal) 0 else 1,
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }

            AiTaskCategory.CAMERA_MOTION_TRACKING -> {
                val msg = visualFxService.apply3DCameraTracking(request.targetClipId ?: "clip_main", "Active Subject Head")
                RouterExecutionResult(
                    message = msg,
                    modifiedFilter = "3D Camera Spatial Track",
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }

            AiTaskCategory.REMOVE_BACKGROUND -> {
                val (msg, bgClip) = visualFxService.removeBackgroundAndReplace(
                    request.targetClipId ?: "clip_main",
                    request.prompt.ifBlank { "Cyberpunk Tokyo rain alleyway" }
                )
                RouterExecutionResult(
                    message = msg,
                    clips = if (bgClip != null) listOf(bgClip) else emptyList(),
                    modifiedFilter = "Magic Alpha Cutout",
                    creditsDeducted = if (isLocal) 0 else 2,
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }

            AiTaskCategory.MOVIE_COLOR_GRADE -> {
                val msg = visualFxService.applyMovieColorGrade(request.prompt.ifBlank { "Oppenheimer Cinematic Film" })
                RouterExecutionResult(
                    message = msg,
                    modifiedFilter = "Film LUT: 35mm Hollywood Grade",
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }

            AiTaskCategory.FACE_BODY_RELIGHTING -> {
                val msg = visualFxService.applyFaceRelighting(request.prompt.ifBlank { "Cyberpunk Neon & Rim Light" })
                RouterExecutionResult(
                    message = msg,
                    modifiedFilter = "AI Studio Light: Neon & Gold Rim",
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }

            AiTaskCategory.KINETIC_TEXT_VFX -> {
                val cap = visualFxService.applyKineticTextFx(request.prompt.ifBlank { "Marvel Fire Dust" })
                RouterExecutionResult(
                    message = "Kinetic text animation applied! Dynamic particles and 3D glass rendering enabled.",
                    captions = listOf(cap),
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }

            AiTaskCategory.FILM_GRAIN_ARTIFACTS -> {
                val msg = visualFxService.applyFilmGrainAndArtifacts(request.prompt.ifBlank { "Kodak 5219 35mm + Light Leaks" })
                RouterExecutionResult(
                    message = msg,
                    modifiedFilter = "Vintage 35mm Grain & Light Leaks",
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }

            AiTaskCategory.SCRIPT_STORYBOARD_GEN -> {
                val sb = workflowService.generateScriptAndStoryboard(request.prompt.ifBlank { "Viral Tech Gear Review" })
                RouterExecutionResult(
                    message = "Gemini 1.5 Pro generated shooting script with 4 scenes, camera angles, and shot list!",
                    details = "${sb.logline} Hook: ${sb.hook}",
                    creditsDeducted = if (isLocal) 0 else 1,
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }

            AiTaskCategory.BRANDING_BUMPER_KIT -> {
                val (intro, outro) = workflowService.generateBrandingBumperKit(request.prompt.ifBlank { "Klipz Creator" })
                RouterExecutionResult(
                    message = "Branding kit applied: Added custom animated Intro & Outro bumpers to timeline!",
                    clips = listOf(intro, outro),
                    modelTargetUsed = if (isLocal) ModelExecutionTarget.LOCAL_ON_DEVICE else ModelExecutionTarget.CLOUD_API
                )
            }
        }
    }
}
