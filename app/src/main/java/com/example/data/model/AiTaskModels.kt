package com.example.data.model

enum class AiTaskCategory(val title: String, val icon: String, val description: String) {
    AUTO_CAPTIONS("Auto Captions & Subtitles", "💬", "Word-by-word synced viral captions with Whisper AI & Gemini"),
    TEXT_BASED_EDITING("Text-Based Editing (Descript)", "📝", "Edit video transcript to auto-trim & compact clips"),
    SMART_CUT("Auto Silence Cut", "✂️", "Detect and cut pauses, silence, and filler words"),
    AUTO_HIGHLIGHT_REEL("Auto Highlight Reel", "🌟", "Condense long footage into punchy viral short videos"),
    SMART_AUTO_REFRAME("1-Click Viral Resizer", "📐", "16:9 to 9:16 / 1:1 auto-reframe with subject tracking"),
    GENERATE_BROLL("Generative B-Roll Video", "🎬", "Create cinematic video clips with Veo 3.1 & Flow"),
    FLUX_IMAGEN_IMAGE("Flux & Imagen 3", "🖼️", "High-res fantasy backgrounds and posters with Flux.1 & Imagen 3"),
    VOICE_CLEANER_STUDIO("Voice Cleaner & Studio Sound", "🎙️", "Convert noisy mobile audio to crystal-clear acoustic studio sound"),
    VOICE_CLONING("Voice Cloning & TTS", "🗣️", "Clone voice & synthesize natural speech in any tone"),
    AI_DUBBING("AI Lip-Sync Dubbing", "🌐", "Dub full video into multi-languages with lip-sync"),
    AI_MUSIC_GEN("AI Music & Beat Sync", "🎵", "Generate royalty-free soundtracks & auto-sync to drops"),
    CAMERA_MOTION_TRACKING("3D Camera & Motion Tracking", "🎯", "Fix 3D text/stickers to moving objects in camera space"),
    REMOVE_BACKGROUND("Green Screen without Screen", "✨", "One-tap background removal with Flow AI background replace"),
    MOVIE_COLOR_GRADE("Movie Color Grade & Match", "🎞️", "Upload film frame screenshot to auto-grade & match multiple clips"),
    FACE_BODY_RELIGHTING("Face & Body Re-Lighting", "💡", "Cyberpunk Neon, Golden Hour, and Studio Softbox relighting"),
    KINETIC_TEXT_VFX("Kinetic Text & Motion Graphics", "⚡", "Marvel style, Fire dust, and 3D Glass kinetic text animations"),
    FILM_GRAIN_ARTIFACTS("Film Artifacts & Grain", "📼", "Vintage 16mm/35mm grain, light leaks, and VCR glitch"),
    SCRIPT_STORYBOARD_GEN("AI Script & Storyboard", "📋", "Generate shooting script + shot list from topic prompt"),
    BRANDING_BUMPER_KIT("Creator Branding Kit", "🏷️", "Custom branded intro, outro bumper, and watermark logo")
}

data class AiModelOption(
    val id: String,
    val name: String,
    val provider: String,
    val tier: String, // "Free / Offline", "1 Credit", "3 Credits"
    val latencyEstimate: String,
    val isRecommended: Boolean = false,
    val isLocalOnDevice: Boolean = false
)

data class AiTaskRequest(
    val taskCategory: AiTaskCategory,
    val selectedModelId: String,
    val prompt: String = "",
    val targetClipId: String? = null,
    val parameters: Map<String, String> = emptyMap()
)

sealed class AiJobState {
    object Idle : AiJobState()
    data class Processing(val progress: Float, val statusMessage: String) : AiJobState()
    data class Success(
        val message: String,
        val generatedClips: List<TimelineClip> = emptyList(),
        val generatedCaptions: List<CaptionItem> = emptyList(),
        val audioBeats: List<AudioBeat> = emptyList(),
        val details: String = ""
    ) : AiJobState()
    data class Error(val errorMessage: String) : AiJobState()
}
