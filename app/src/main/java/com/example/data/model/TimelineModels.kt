package com.example.data.model

import androidx.annotation.DrawableRes
import com.example.R

enum class AspectRatio(val label: String, val ratio: Float, val iconName: String) {
    RATIO_9_16("9:16 Reels/TikTok", 9f / 16f, "📱"),
    RATIO_16_9("16:9 Landscape", 16f / 9f, "🖥️"),
    RATIO_1_1("1:1 Square", 1f, "⏹️"),
    RATIO_4_5("4:5 Feed", 4f / 5f, "🖼️")
}

enum class TrackType(val displayName: String) {
    TEXT("Text & Captions"),
    OVERLAY("PiP & Overlays"),
    VIDEO("Main Video"),
    AUDIO("Music & Beats"),
    SFX("Voice & FX")
}

enum class TransitionType(val displayName: String) {
    NONE("None"),
    DISSOLVE("Cross Dissolve"),
    GLITCH("Cyber Glitch"),
    ZOOM_IN("Dynamic Zoom"),
    SLIDE_LEFT("Slide In"),
    WHIP_PAN("Whip Pan")
}

enum class CaptionStyle(val displayName: String) {
    MR_BEAST("Bold Viral"),
    NEON("Neon Glow"),
    KARAOKE("Word Highlight"),
    MINIMAL("Clean Subtitle")
}

data class CaptionWord(
    val word: String,
    val startOffsetMs: Long,
    val endOffsetMs: Long
)

data class CaptionItem(
    val id: String,
    val startTimeMs: Long,
    val durationMs: Long,
    val fullText: String,
    val words: List<CaptionWord> = emptyList(),
    val style: CaptionStyle = CaptionStyle.MR_BEAST
)

data class TimelineClip(
    val id: String,
    val title: String,
    @param:DrawableRes val drawableResId: Int,
    val startTimeMs: Long,
    val durationMs: Long,
    val sourceDurationMs: Long = durationMs,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val opacity: Float = 1.0f,
    val scale: Float = 1.0f,
    val filterName: String = "Normal",
    val transitionIn: TransitionType = TransitionType.NONE,
    val isAiGenerated: Boolean = false,
    val hasRemovedBackground: Boolean = false
)

data class TimelineTrack(
    val id: String,
    val name: String,
    val type: TrackType,
    val isMuted: Boolean = false,
    val isLocked: Boolean = false,
    val clips: List<TimelineClip> = emptyList(),
    val captions: List<CaptionItem> = emptyList()
)

data class AudioBeat(
    val timestampMs: Long,
    val intensity: Float // 0f to 1f
)

data class Project(
    val id: String,
    val name: String,
    val aspectRatio: AspectRatio = AspectRatio.RATIO_9_16,
    val fps: Int = 30,
    val totalDurationMs: Long = 12000L,
    val tracks: List<TimelineTrack> = emptyList(),
    val beats: List<AudioBeat> = emptyList(),
    @param:DrawableRes val thumbnailResId: Int = R.drawable.sample_skater,
    val lastModified: String = "Just now"
)
