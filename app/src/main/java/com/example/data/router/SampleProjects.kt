package com.example.data.router

import com.example.R
import com.example.data.model.AspectRatio
import com.example.data.model.AudioBeat
import com.example.data.model.CaptionItem
import com.example.data.model.CaptionStyle
import com.example.data.model.CaptionWord
import com.example.data.model.Project
import com.example.data.model.TimelineClip
import com.example.data.model.TimelineTrack
import com.example.data.model.TrackType
import com.example.data.model.TransitionType

object SampleProjects {

    fun createDefaultProject(): Project {
        val captions = listOf(
            CaptionItem(
                id = "cap_1",
                startTimeMs = 0L,
                durationMs = 2800L,
                fullText = "THIS SUMMER HIT DIFFERENT",
                style = CaptionStyle.MR_BEAST,
                words = listOf(
                    CaptionWord("THIS", 0L, 600L),
                    CaptionWord("SUMMER", 600L, 1400L),
                    CaptionWord("HIT", 1400L, 2000L),
                    CaptionWord("DIFFERENT", 2000L, 2800L)
                )
            ),
            CaptionItem(
                id = "cap_2",
                startTimeMs = 3000L,
                durationMs = 3200L,
                fullText = "MAX SPEED NO LIMITS",
                style = CaptionStyle.NEON,
                words = listOf(
                    CaptionWord("MAX", 3000L, 3800L),
                    CaptionWord("SPEED", 3800L, 4600L),
                    CaptionWord("NO", 4600L, 5200L),
                    CaptionWord("LIMITS", 5200L, 6200L)
                )
            ),
            CaptionItem(
                id = "cap_3",
                startTimeMs = 6400L,
                durationMs = 3600L,
                fullText = "AI POWERED CREATIVITY",
                style = CaptionStyle.MR_BEAST,
                words = listOf(
                    CaptionWord("AI", 6400L, 7200L),
                    CaptionWord("POWERED", 7200L, 8200L),
                    CaptionWord("CREATIVITY", 8200L, 10000L)
                )
            )
        )

        val mainVideoClips = listOf(
            TimelineClip(
                id = "clip_skate",
                title = "Skate_DropIn_01.mp4",
                drawableResId = R.drawable.sample_skater,
                startTimeMs = 0L,
                durationMs = 3800L,
                sourceDurationMs = 5000L,
                speed = 1.0f,
                filterName = "Vibrant Boost",
                transitionIn = TransitionType.NONE
            ),
            TimelineClip(
                id = "clip_motocross",
                title = "Moto_Burnout_Speed.mp4",
                drawableResId = R.drawable.sample_motocross,
                startTimeMs = 3800L,
                durationMs = 3400L,
                sourceDurationMs = 4500L,
                speed = 1.25f,
                filterName = "High Action",
                transitionIn = TransitionType.GLITCH
            ),
            TimelineClip(
                id = "clip_portrait",
                title = "GoldenHour_Smile.mp4",
                drawableResId = R.drawable.sample_portrait,
                startTimeMs = 7200L,
                durationMs = 3800L,
                sourceDurationMs = 5000L,
                speed = 1.0f,
                filterName = "Warm Film",
                transitionIn = TransitionType.DISSOLVE
            )
        )

        val pipOverlayClips = listOf(
            TimelineClip(
                id = "clip_pip_astro",
                title = "Astro_Reaction_PiP.mp4",
                drawableResId = R.drawable.sample_astronaut,
                startTimeMs = 1500L,
                durationMs = 2800L,
                speed = 1.0f,
                scale = 0.45f,
                opacity = 0.95f,
                hasRemovedBackground = true,
                isAiGenerated = true
            )
        )

        val audioClips = listOf(
            TimelineClip(
                id = "clip_bgm",
                title = "Midnight_Synthwave_128BPM.mp3",
                drawableResId = R.drawable.klipz_icon_fg,
                startTimeMs = 0L,
                durationMs = 11000L,
                volume = 0.85f
            )
        )

        val sfxClips = listOf(
            TimelineClip(
                id = "clip_sfx_whoosh",
                title = "Whoosh_Transition.wav",
                drawableResId = R.drawable.klipz_icon_fg,
                startTimeMs = 3700L,
                durationMs = 800L,
                volume = 1.0f
            ),
            TimelineClip(
                id = "clip_sfx_glitch",
                title = "Digital_Impact_Hit.wav",
                drawableResId = R.drawable.klipz_icon_fg,
                startTimeMs = 7100L,
                durationMs = 900L,
                volume = 0.9f
            )
        )

        val beats = listOf(
            AudioBeat(500L, 0.6f),
            AudioBeat(1200L, 0.9f),
            AudioBeat(1900L, 0.6f),
            AudioBeat(2600L, 0.95f),
            AudioBeat(3300L, 0.6f),
            AudioBeat(3800L, 1.0f), // Cut sync
            AudioBeat(4500L, 0.7f),
            AudioBeat(5200L, 0.95f),
            AudioBeat(5900L, 0.6f),
            AudioBeat(6600L, 0.9f),
            AudioBeat(7200L, 1.0f), // Cut sync
            AudioBeat(7900L, 0.7f),
            AudioBeat(8600L, 0.9f),
            AudioBeat(9300L, 0.6f),
            AudioBeat(10000L, 0.95f)
        )

        val tracks = listOf(
            TimelineTrack(
                id = "track_text",
                name = "Captions & Titles",
                type = TrackType.TEXT,
                captions = captions
            ),
            TimelineTrack(
                id = "track_overlay",
                name = "PiP & Magic Cutouts",
                type = TrackType.OVERLAY,
                clips = pipOverlayClips
            ),
            TimelineTrack(
                id = "track_video",
                name = "Main Video Track",
                type = TrackType.VIDEO,
                clips = mainVideoClips
            ),
            TimelineTrack(
                id = "track_audio",
                name = "Music & Beat Sync",
                type = TrackType.AUDIO,
                clips = audioClips
            ),
            TimelineTrack(
                id = "track_sfx",
                name = "Sound Effects (SFX)",
                type = TrackType.SFX,
                clips = sfxClips
            )
        )

        return Project(
            id = "proj_skate_reel",
            name = "Summer Skate Reel",
            aspectRatio = AspectRatio.RATIO_9_16,
            fps = 30,
            totalDurationMs = 11000L,
            tracks = tracks,
            beats = beats,
            thumbnailResId = R.drawable.sample_skater,
            lastModified = "Saved just now"
        )
    }

    fun createSciFiProject(): Project {
        val astroClips = listOf(
            TimelineClip(
                id = "clip_astro_main",
                title = "Cosmic_Rider_Veo3.mp4",
                drawableResId = R.drawable.sample_astronaut,
                startTimeMs = 0L,
                durationMs = 6000L,
                isAiGenerated = true,
                filterName = "Cinematic Sci-Fi"
            )
        )

        return Project(
            id = "proj_scifi",
            name = "Space Dunes Teaser",
            aspectRatio = AspectRatio.RATIO_9_16,
            fps = 24,
            totalDurationMs = 6000L,
            tracks = listOf(
                TimelineTrack(
                    id = "track_video_scifi",
                    name = "Main Video",
                    type = TrackType.VIDEO,
                    clips = astroClips
                )
            ),
            thumbnailResId = R.drawable.sample_astronaut,
            lastModified = "1 hour ago"
        )
    }
}
