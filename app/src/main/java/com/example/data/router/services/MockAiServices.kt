package com.example.data.router.services

import com.example.R
import com.example.data.model.CaptionItem
import com.example.data.model.CaptionStyle
import com.example.data.model.CaptionWord
import com.example.data.model.TimelineClip
import com.example.data.model.TransitionType
import com.example.data.router.AudioProcessingService
import com.example.data.router.CaptioningService
import com.example.data.router.ImageGenerationService
import com.example.data.router.ImageResult
import com.example.data.router.ModelExecutionTarget
import com.example.data.router.SmartEditingService
import com.example.data.router.StoryboardData
import com.example.data.router.StoryboardScene
import com.example.data.router.VideoGenerationService
import com.example.data.router.VideoResult
import com.example.data.router.VisualFxService
import com.example.data.router.WorkflowService
import kotlinx.coroutines.delay

class MockImageGenerationService : ImageGenerationService {
    override suspend fun generateImage(
        prompt: String,
        modelId: String,
        target: ModelExecutionTarget
    ): ImageResult {
        delay(if (target == ModelExecutionTarget.LOCAL_ON_DEVICE) 350 else 750)
        val isFlux = modelId.contains("flux")
        val provider = if (isFlux) "Flux.1 / Hugging Face Open API" else "Google Imagen 3 (Free Tier)"
        val drawable = if (prompt.contains("skate", ignoreCase = true)) {
            R.drawable.sample_skater
        } else if (prompt.contains("motor", ignoreCase = true) || prompt.contains("bike", ignoreCase = true)) {
            R.drawable.sample_motocross
        } else {
            R.drawable.sample_astronaut
        }
        return ImageResult(
            title = if (prompt.isNotBlank()) "AI_${prompt.take(16)}.png" else "Flux_Fantasy_Backdrop.png",
            drawableResId = drawable,
            provider = provider,
            prompt = prompt
        )
    }
}

class MockVideoGenerationService : VideoGenerationService {
    override suspend fun generateVideo(
        prompt: String,
        durationMs: Long,
        modelId: String,
        target: ModelExecutionTarget
    ): VideoResult {
        delay(if (target == ModelExecutionTarget.LOCAL_ON_DEVICE) 450 else 950)
        val clip = TimelineClip(
            id = "veo_broll_${System.currentTimeMillis()}",
            title = if (prompt.isNotBlank()) "Veo_${prompt.take(14)}.mp4" else "Veo_Cinematic_Broll.mp4",
            drawableResId = R.drawable.sample_astronaut,
            startTimeMs = 3800L,
            durationMs = durationMs.coerceIn(2000L, 8000L),
            sourceDurationMs = durationMs.coerceIn(2000L, 8000L),
            isAiGenerated = true,
            transitionIn = TransitionType.ZOOM_IN,
            filterName = "Veo 3.1 Cinematic"
        )
        return VideoResult(clip = clip, resolution = "4K UHD 3840x2160", fps = 30)
    }
}

class MockCaptioningService : CaptioningService {
    override suspend fun generateCaptions(
        audioClipId: String?,
        modelId: String,
        target: ModelExecutionTarget
    ): List<CaptionItem> {
        delay(if (target == ModelExecutionTarget.LOCAL_ON_DEVICE) 300 else 700)
        return listOf(
            CaptionItem(
                id = "ai_cap_1",
                startTimeMs = 0L,
                durationMs = 3500L,
                fullText = "THIS VIDEO WAS EDITED WITH KLIPZ",
                style = CaptionStyle.MR_BEAST,
                words = listOf(
                    CaptionWord("THIS", 0L, 500L),
                    CaptionWord("VIDEO", 500L, 1100L),
                    CaptionWord("WAS", 1100L, 1500L),
                    CaptionWord("EDITED", 1500L, 2200L),
                    CaptionWord("WITH", 2200L, 2600L),
                    CaptionWord("KLIPZ", 2600L, 3500L)
                )
            ),
            CaptionItem(
                id = "ai_cap_2",
                startTimeMs = 3600L,
                durationMs = 3800L,
                fullText = "ZERO WATERMARKS MAXIMUM CREATIVITY",
                style = CaptionStyle.NEON,
                words = listOf(
                    CaptionWord("ZERO", 3600L, 4300L),
                    CaptionWord("WATERMARKS", 4300L, 5400L),
                    CaptionWord("MAXIMUM", 5400L, 6300L),
                    CaptionWord("CREATIVITY", 6300L, 7400L)
                )
            )
        )
    }
}

class MockSmartEditingService : SmartEditingService {
    override suspend fun applySilenceCut(clips: List<TimelineClip>): Pair<List<TimelineClip>, Long> {
        delay(400)
        var totalRemoved = 0L
        val trimmed = clips.map { clip ->
            if (clip.durationMs > 2500L) {
                val cut = 600L
                totalRemoved += cut
                clip.copy(durationMs = clip.durationMs - cut)
            } else clip
        }
        return trimmed to (if (totalRemoved == 0L) 1200L else totalRemoved)
    }

    override suspend fun applyTextBasedEdit(
        removedWordOffsets: List<LongRange>,
        clip: TimelineClip
    ): TimelineClip {
        delay(250)
        val trimMs = removedWordOffsets.size * 350L
        return clip.copy(
            durationMs = (clip.durationMs - trimMs).coerceAtLeast(1000L)
        )
    }

    override suspend fun generateHighlightReel(clips: List<TimelineClip>): List<TimelineClip> {
        delay(600)
        return clips.take(3).mapIndexed { idx, clip ->
            clip.copy(
                id = "highlight_${clip.id}",
                title = "Highlight_Hook_${idx + 1}.mp4",
                durationMs = 2500L,
                speed = 1.2f,
                transitionIn = TransitionType.GLITCH
            )
        }
    }

    override suspend fun applyAutoReframe(targetAspectLabel: String): String {
        delay(350)
        return "Smart Auto-Reframe active: AI Subject Face & Body Panning calibrated for $targetAspectLabel."
    }

    override suspend fun syncToBeats(
        audioTrackId: String,
        clips: List<TimelineClip>
    ): List<TimelineClip> {
        delay(500)
        return clips.mapIndexed { index, clip ->
            val rampSpeed = if (index % 2 == 0) 1.25f else 0.85f
            clip.copy(
                speed = rampSpeed,
                transitionIn = TransitionType.ZOOM_IN
            )
        }
    }
}

class MockAudioProcessingService : AudioProcessingService {
    override suspend fun enhanceVoiceStudioSound(clipId: String): String {
        delay(500)
        return "Voice Cleaner applied: Room echo eliminated, -28dB noise floor suppression, acoustic tube warmth enabled."
    }

    override suspend fun cloneVoiceAndSynthesize(script: String, voiceName: String): TimelineClip {
        delay(650)
        return TimelineClip(
            id = "tts_cloned_${System.currentTimeMillis()}",
            title = "ClonedVoice_${voiceName.take(8)}.wav",
            drawableResId = R.drawable.klipz_icon_fg,
            startTimeMs = 0L,
            durationMs = 4800L,
            volume = 1.0f
        )
    }

    override suspend fun dubVideo(targetLang: String, originalClipId: String): TimelineClip {
        delay(800)
        return TimelineClip(
            id = "dub_${targetLang.lowercase()}_${System.currentTimeMillis()}",
            title = "Dubbed_${targetLang}_LipSync.wav",
            drawableResId = R.drawable.klipz_icon_fg,
            startTimeMs = 0L,
            durationMs = 5000L,
            volume = 1.0f
        )
    }

    override suspend fun generateAiMusic(genre: String, bpm: Int, durationMs: Long): TimelineClip {
        delay(700)
        return TimelineClip(
            id = "ai_music_${System.currentTimeMillis()}",
            title = "AI_${genre}_${bpm}BPM.mp3",
            drawableResId = R.drawable.klipz_icon_fg,
            startTimeMs = 0L,
            durationMs = durationMs.coerceAtLeast(8000L),
            volume = 0.85f
        )
    }
}

class MockVisualFxService : VisualFxService {
    override suspend fun apply3DCameraTracking(targetClipId: String, trackedObject: String): String {
        delay(550)
        return "3D Camera Tracking locked on $trackedObject: 6-DoF orientation matrix anchored with perspective parallax."
    }

    override suspend fun removeBackgroundAndReplace(
        targetClipId: String,
        backgroundPrompt: String
    ): Pair<String, TimelineClip?> {
        delay(650)
        val bgClip = TimelineClip(
            id = "flux_bg_${System.currentTimeMillis()}",
            title = "Flux_Background.png",
            drawableResId = R.drawable.sample_astronaut,
            startTimeMs = 0L,
            durationMs = 6000L,
            filterName = "Flux Neural Backdrop"
        )
        return "Green Screen removed without screen: AI alpha mask isolated subject & inserted Flux AI backdrop." to bgClip
    }

    override suspend fun applyMovieColorGrade(paletteName: String): String {
        delay(400)
        return "Movie Color Grade applied: $paletteName (3D LUT 64-cube matched with cinematic contrast & grain)."
    }

    override suspend fun applyFaceRelighting(lightPreset: String): String {
        delay(450)
        return "Face & Body Re-Lighting calibrated: $lightPreset with ray-traced normal map shading."
    }

    override suspend fun applyKineticTextFx(textStyle: String): CaptionItem {
        delay(400)
        return CaptionItem(
            id = "kinetic_fx_${System.currentTimeMillis()}",
            startTimeMs = 1200L,
            durationMs = 3000L,
            fullText = "EPIC $textStyle EFFECT",
            style = CaptionStyle.NEON,
            words = listOf(
                CaptionWord("EPIC", 1200L, 1800L),
                CaptionWord(textStyle.uppercase(), 1800L, 2500L),
                CaptionWord("EFFECT", 2500L, 4200L)
            )
        )
    }

    override suspend fun applyFilmGrainAndArtifacts(filmStock: String): String {
        delay(350)
        return "Film Stock applied: $filmStock (Organic 35mm grain, vintage halation & light leak gate weave)."
    }
}

class MockWorkflowService : WorkflowService {
    override suspend fun generateScriptAndStoryboard(topic: String): StoryboardData {
        delay(700)
        val safeTopic = if (topic.isNotBlank()) topic else "Tech Gadget Showcase"
        return StoryboardData(
            topic = safeTopic,
            logline = "A high-retention viral short revealing the secret power of $safeTopic in under 30 seconds.",
            hook = "Stop scrolling! Here is why $safeTopic is changing everything.",
            scenes = listOf(
                StoryboardScene(
                    sceneNumber = 1,
                    title = "The Explosive Hook",
                    shotType = "Extreme Close-Up (ECU) with Whip Pan",
                    visualPrompt = "Subject holding product with dynamic kinetic text and neon rim lighting.",
                    narration = "You won't believe what happens when you turn this on for the first time!",
                    durationSec = 4
                ),
                StoryboardScene(
                    sceneNumber = 2,
                    title = "The Problem & Agitation",
                    shotType = "Medium Shot (MS) 9:16 vertical",
                    visualPrompt = "Quick montage showing previous frustrations with normal alternatives.",
                    narration = "Normally you'd waste hours fixing this, but watch this one simple trick.",
                    durationSec = 6
                ),
                StoryboardScene(
                    sceneNumber = 3,
                    title = "The AI Magic Reveal",
                    shotType = "Fast Orbit 3D Camera Tracking",
                    visualPrompt = "Product glows with cyberpunk neon holographic particles.",
                    narration = "With built-in AI intelligence, one tap completely handles the workflow.",
                    durationSec = 7
                ),
                StoryboardScene(
                    sceneNumber = 4,
                    title = "Viral Call To Action",
                    shotType = "Selfie Cam with Arrow Pointer",
                    visualPrompt = "Animated sticker and like/follow bumper popping onto screen.",
                    narration = "Save this video right now before you forget, and follow for part two!",
                    durationSec = 5
                )
            )
        )
    }

    override suspend fun generateBrandingBumperKit(creatorName: String): Pair<TimelineClip, TimelineClip> {
        delay(500)
        val intro = TimelineClip(
            id = "brand_intro_${System.currentTimeMillis()}",
            title = "Branded_Intro_${creatorName.take(6)}.mp4",
            drawableResId = R.drawable.klipz_icon_fg,
            startTimeMs = 0L,
            durationMs = 2000L,
            filterName = "Branded Stinger"
        )
        val outro = TimelineClip(
            id = "brand_outro_${System.currentTimeMillis()}",
            title = "Branded_Outro_CTA.mp4",
            drawableResId = R.drawable.klipz_icon_fg,
            startTimeMs = 10000L,
            durationMs = 2500L,
            filterName = "Subscribe Bumper"
        )
        return intro to outro
    }
}
