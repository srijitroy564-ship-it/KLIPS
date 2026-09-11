package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioBeat
import com.example.data.model.CaptionItem
import com.example.data.model.Project
import com.example.data.model.TimelineClip
import com.example.data.model.TimelineTrack
import com.example.data.model.TrackType
import com.example.data.model.TransitionType
import com.example.ui.theme.AudioTrackColor
import com.example.ui.theme.BeatMarkerColor
import com.example.ui.theme.BrandDeepBlue
import com.example.ui.theme.BrandMagenta
import com.example.ui.theme.BrandViolet
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.OverlayTrackColor
import com.example.ui.theme.PlayheadColor
import com.example.ui.theme.PlayheadGuide
import com.example.ui.theme.SelectionGold
import com.example.ui.theme.SfxTrackColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTrackColor
import com.example.ui.theme.VideoTrackColor
import kotlin.math.roundToInt

@Composable
fun TimelineView(
    project: Project,
    playheadPositionMs: Long,
    zoomScale: Float,
    selectedClipId: String?,
    onSeekTo: (Long) -> Unit,
    onSelectClip: (String?) -> Unit,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // Pixel scale: base 0.08 dp per millisecond multiplied by zoomScale (1 second = 80dp at 1.0x)
    val dpPerMs = 0.08f * zoomScale
    val totalTimelineWidthDp = (project.totalDurationMs * dpPerMs).coerceAtLeast(360f).dp

    val scrollState = rememberScrollState()

    // Auto-scroll timeline to keep playhead in view during playback
    LaunchedEffect(playheadPositionMs) {
        val playheadOffsetPx = (playheadPositionMs * dpPerMs * 2.7f).roundToInt()
        val visibleWidth = scrollState.viewportSize
        if (visibleWidth > 0) {
            val half = visibleWidth / 2
            val targetScroll = (playheadOffsetPx - half).coerceAtLeast(0)
            if (kotlin.math.abs(scrollState.value - targetScroll) > visibleWidth / 3) {
                scrollState.animateScrollTo(targetScroll)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .border(width = 1.dp, color = DarkBorder)
    ) {
        // Timeline Header: Track Header label & Zoom scale controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .background(DarkSurfaceElevated)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = null,
                    tint = ElectricIndigo,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Timeline (${project.tracks.size} Tracks)",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Timeline Zoom Slider
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onZoomChange((zoomScale - 0.25f).coerceAtLeast(0.5f)) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }

                Slider(
                    value = zoomScale,
                    onValueChange = onZoomChange,
                    valueRange = 0.5f..2.5f,
                    modifier = Modifier
                        .width(90.dp)
                        .testTag("timeline_zoom_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricIndigo,
                        activeTrackColor = ElectricIndigo,
                        inactiveTrackColor = DarkBorder
                    )
                )

                IconButton(
                    onClick = { onZoomChange((zoomScale + 0.25f).coerceAtMost(2.5f)) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Timeline Body: Left Track Headers (76dp fixed) + Right Scrollable Tracks
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .height(220.dp)
        ) {
            // Left fixed track icons/labels
            TrackHeadersColumn(project.tracks)

            // Right scrollable timeline ruler & multi-tracks with Playhead
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .horizontalScroll(scrollState)
            ) {
                // Scrollable Tracks Container
                Column(
                    modifier = Modifier
                        .width(totalTimelineWidthDp)
                        .fillMaxHeight()
                ) {
                    // 1. Time Ruler (0s, 1s, 2s, 3s...)
                    TimelineRuler(
                        totalDurationMs = project.totalDurationMs,
                        dpPerMs = dpPerMs,
                        beats = project.beats,
                        onSeekTo = onSeekTo
                    )

                    // 2. Multi-Tracks Stack
                    project.tracks.forEach { track ->
                        TimelineTrackRow(
                            track = track,
                            dpPerMs = dpPerMs,
                            selectedClipId = selectedClipId,
                            onSelectClip = onSelectClip,
                            onSeekTo = onSeekTo
                        )
                    }
                }

                // 3. Prominent Red/Electric Playhead
                val playheadOffsetDp = (playheadPositionMs * dpPerMs).dp

                Box(
                    modifier = Modifier
                        .offset(x = playheadOffsetDp)
                        .fillMaxHeight()
                        .width(24.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val deltaMs = (dragAmount.x / dpPerMs).toLong()
                                onSeekTo((playheadPositionMs + deltaMs).coerceIn(0L, project.totalDurationMs))
                            }
                        }
                        .testTag("timeline_playhead"),
                    contentAlignment = Alignment.TopCenter
                ) {
                    // Playhead vertical line
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(2.dp)
                            .background(PlayheadColor)
                    )

                    // Playhead top scrubbing badge handle
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(PlayheadColor)
                            .border(2.dp, Color.White, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackHeadersColumn(tracks: List<TimelineTrack>) {
    Column(
        modifier = Modifier
            .width(76.dp)
            .fillMaxHeight()
            .background(DarkCard)
            .border(width = 1.dp, color = DarkBorder)
    ) {
        // Header space for Ruler alignment
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .background(DarkSurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Text("TRACKS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }

        // Track items
        tracks.forEach { track ->
            val icon: ImageVector = when (track.type) {
                TrackType.TEXT -> Icons.Default.TextFields
                TrackType.OVERLAY -> Icons.Default.Layers
                TrackType.VIDEO -> Icons.Default.Movie
                TrackType.AUDIO -> Icons.Default.GraphicEq
                TrackType.SFX -> Icons.AutoMirrored.Filled.VolumeUp
            }

            val trackColor = when (track.type) {
                TrackType.TEXT -> TextTrackColor
                TrackType.OVERLAY -> OverlayTrackColor
                TrackType.VIDEO -> VideoTrackColor
                TrackType.AUDIO -> AudioTrackColor
                TrackType.SFX -> SfxTrackColor
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .border(width = 0.5.dp, color = DarkBorder)
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(trackColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = icon,
                        contentDescription = track.name,
                        tint = trackColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (track.type) {
                            TrackType.TEXT -> "Text"
                            TrackType.OVERLAY -> "PiP"
                            TrackType.VIDEO -> "Video"
                            TrackType.AUDIO -> "Audio"
                            TrackType.SFX -> "SFX"
                        },
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineRuler(
    totalDurationMs: Long,
    dpPerMs: Float,
    beats: List<AudioBeat>,
    onSeekTo: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(26.dp)
            .background(DarkSurfaceElevated)
            .clickable { /* handled by drag/seek */ }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val targetMs = (change.position.x / dpPerMs).toLong()
                    onSeekTo(targetMs.coerceIn(0L, totalDurationMs))
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val totalSeconds = (totalDurationMs / 1000).toInt() + 1
            for (sec in 0..totalSeconds) {
                val x = sec * 1000f * dpPerMs

                // Major tick mark
                drawLine(
                    color = Color(0x66FFFFFF),
                    start = Offset(x, size.height - 8f),
                    end = Offset(x, size.height),
                    strokeWidth = 2f
                )

                // Half-second tick
                val halfX = (sec * 1000f + 500f) * dpPerMs
                drawLine(
                    color = Color(0x33FFFFFF),
                    start = Offset(halfX, size.height - 4f),
                    end = Offset(halfX, size.height),
                    strokeWidth = 1f
                )
            }

            // Glowing Beat markers
            beats.forEach { beat ->
                val beatX = beat.timestampMs * dpPerMs
                drawCircle(
                    color = BeatMarkerColor.copy(alpha = beat.intensity),
                    radius = 3f,
                    center = Offset(beatX, size.height / 2)
                )
            }
        }

        // Time labels (0s, 1s, 2s...)
        val totalSecs = (totalDurationMs / 1000).toInt()
        for (sec in 0..totalSecs) {
            val labelOffset = (sec * 1000f * dpPerMs).dp
            Text(
                text = "${sec}s",
                color = TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .offset(x = labelOffset + 2.dp, y = 2.dp)
            )
        }
    }
}

@Composable
private fun TimelineTrackRow(
    track: TimelineTrack,
    dpPerMs: Float,
    selectedClipId: String?,
    onSelectClip: (String?) -> Unit,
    onSeekTo: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .border(width = 0.5.dp, color = DarkBorder)
            .background(DarkBg.copy(alpha = 0.6f))
            .clickable {
                onSelectClip(null)
            }
    ) {
        // Render Captions if TEXT track
        if (track.type == TrackType.TEXT) {
            track.captions.forEach { caption ->
                CaptionBlock(
                    caption = caption,
                    dpPerMs = dpPerMs,
                    onClick = { onSeekTo(caption.startTimeMs) }
                )
            }
        }

        // Render audio waveform placeholder if AUDIO track
        if (track.type == TrackType.AUDIO || track.type == TrackType.SFX) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val step = 10f
                var x = 0f
                while (x < size.width) {
                    val barHeight = ((kotlin.math.sin(x * 0.05f) + 1.2f) * 7f).coerceIn(2f, 14f)
                    drawLine(
                        color = if (track.type == TrackType.AUDIO) AudioTrackColor.copy(alpha = 0.35f) else SfxTrackColor.copy(alpha = 0.35f),
                        start = Offset(x, size.height / 2 - barHeight),
                        end = Offset(x, size.height / 2 + barHeight),
                        strokeWidth = 2f
                    )
                    x += step
                }
            }
        }

        // Render Clips
        track.clips.forEach { clip ->
            ClipItemBlock(
                clip = clip,
                trackType = track.type,
                dpPerMs = dpPerMs,
                isSelected = clip.id == selectedClipId,
                onSelect = { onSelectClip(clip.id) },
                onSeek = { onSeekTo(clip.startTimeMs) }
            )
        }
    }
}

@Composable
private fun ClipItemBlock(
    clip: TimelineClip,
    trackType: TrackType,
    dpPerMs: Float,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onSeek: () -> Unit
) {
    val clipOffset = (clip.startTimeMs * dpPerMs).dp
    val clipWidth = (clip.durationMs * dpPerMs).dp.coerceAtLeast(30.dp)

    val blockColor = when (trackType) {
        TrackType.VIDEO -> VideoTrackColor
        TrackType.OVERLAY -> OverlayTrackColor
        TrackType.AUDIO -> AudioTrackColor
        TrackType.SFX -> SfxTrackColor
        TrackType.TEXT -> TextTrackColor
    }

    Box(
        modifier = Modifier
            .offset(x = clipOffset)
            .width(clipWidth)
            .height(34.dp)
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(blockColor.copy(alpha = 0.85f))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) SelectionGold else Color(0x33FFFFFF),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable {
                onSelect()
                onSeek()
            }
            .testTag("clip_item_${clip.id}"),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Trim Handle Left (when selected)
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(8.dp)
                        .background(SelectionGold),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(2.dp, 12.dp).background(Color.Black))
                }
            }

            // Thumbnail preview for video or icon
            if (trackType == TrackType.VIDEO || trackType == TrackType.OVERLAY) {
                Image(
                    painter = painterResource(id = clip.drawableResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            // Clip Title & Badges
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = clip.title,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (clip.speed != 1.0f) {
                        Text(
                            text = "${clip.speed}x",
                            color = SelectionGold,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    if (clip.transitionIn != TransitionType.NONE) {
                        Text(
                            text = "[⚡ ${clip.transitionIn.displayName}]",
                            color = Color(0xFF00E5FF),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (clip.isAiGenerated) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Generated",
                            tint = SelectionGold,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            // Trim Handle Right (when selected)
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(8.dp)
                        .background(SelectionGold),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(2.dp, 12.dp).background(Color.Black))
                }
            }
        }
    }
}

@Composable
private fun CaptionBlock(
    caption: CaptionItem,
    dpPerMs: Float,
    onClick: () -> Unit
) {
    val blockOffset = (caption.startTimeMs * dpPerMs).dp
    val blockWidth = (caption.durationMs * dpPerMs).dp.coerceAtLeast(40.dp)

    Box(
        modifier = Modifier
            .offset(x = blockOffset)
            .width(blockWidth)
            .height(30.dp)
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(TextTrackColor.copy(alpha = 0.8f))
            .border(1.dp, BrandMagenta, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "💬 ${caption.fullText}",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
