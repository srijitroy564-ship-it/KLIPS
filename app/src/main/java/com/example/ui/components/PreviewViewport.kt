package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AspectRatio as ProjectRatio
import com.example.data.model.CaptionItem
import com.example.data.model.CaptionStyle
import com.example.data.model.Project
import com.example.data.model.TimelineClip
import com.example.data.model.TrackType
import com.example.ui.theme.BrandDeepBlue
import com.example.ui.theme.BrandMagenta
import com.example.ui.theme.BrandViolet
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.KlipzAccentGradient
import com.example.ui.theme.SelectionGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun PreviewViewport(
    project: Project,
    playheadPositionMs: Long,
    isPlaying: Boolean,
    safeMarginsEnabled: Boolean,
    onTogglePlayPause: () -> Unit,
    onStepFrame: (Boolean) -> Unit,
    onSeekToStart: () -> Unit,
    onToggleSafeMargins: () -> Unit,
    onSelectAspectRatio: (ProjectRatio) -> Unit,
    modifier: Modifier = Modifier
) {
    var showRatioMenu by remember { mutableStateOf(false) }

    // Find active clip at current playhead from main video track
    val activeVideoClip: TimelineClip? = remember(project, playheadPositionMs) {
        val videoTrack = project.tracks.find { it.type == TrackType.VIDEO }
        videoTrack?.clips?.find {
            playheadPositionMs >= it.startTimeMs && playheadPositionMs < (it.startTimeMs + it.durationMs)
        } ?: videoTrack?.clips?.firstOrNull()
    }

    // Find active overlay clip (e.g. PiP astronaut or sticker)
    val activeOverlayClip: TimelineClip? = remember(project, playheadPositionMs) {
        val overlayTrack = project.tracks.find { it.type == TrackType.OVERLAY }
        overlayTrack?.clips?.find {
            playheadPositionMs >= it.startTimeMs && playheadPositionMs < (it.startTimeMs + it.durationMs)
        }
    }

    // Find active caption at current playhead
    val activeCaption: CaptionItem? = remember(project, playheadPositionMs) {
        val textTrack = project.tracks.find { it.type == TrackType.TEXT }
        textTrack?.captions?.find {
            playheadPositionMs >= it.startTimeMs && playheadPositionMs < (it.startTimeMs + it.durationMs)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkBg)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top auxiliary bar: Aspect Ratio Selector & Safe Guides Toggle & Timecode
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Aspect Ratio badge dropdown
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceElevated)
                        .clickable { showRatioMenu = true }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("aspect_ratio_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AspectRatio,
                        contentDescription = "Aspect Ratio",
                        tint = ElectricIndigo,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = project.aspectRatio.label.substringBefore(" "),
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                DropdownMenu(
                    expanded = showRatioMenu,
                    onDismissRequest = { showRatioMenu = false },
                    modifier = Modifier.background(DarkCard)
                ) {
                    ProjectRatio.entries.forEach { ratio ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "${ratio.iconName} ${ratio.label}",
                                    color = if (project.aspectRatio == ratio) ElectricIndigo else TextPrimary,
                                    fontWeight = if (project.aspectRatio == ratio) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onSelectAspectRatio(ratio)
                                showRatioMenu = false
                            }
                        )
                    }
                }
            }

            // Timecode display: 00:02.40 / 00:11.00
            Text(
                text = "${formatTimecode(playheadPositionMs)} / ${formatTimecode(project.totalDurationMs)}",
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            )

            // Safe Margins Guide Toggle
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (safeMarginsEnabled) Color(0x335B4CFF) else DarkSurfaceElevated)
                    .border(
                        width = 1.dp,
                        color = if (safeMarginsEnabled) ElectricIndigo else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onToggleSafeMargins() }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("safe_margins_toggle"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FitScreen,
                    contentDescription = "Safe Zones",
                    tint = if (safeMarginsEnabled) ElectricIndigo else TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Guides",
                    color = if (safeMarginsEnabled) TextPrimary else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Preview Canvas Box with dynamic Aspect Ratio
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF070709))
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(project.aspectRatio.ratio)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                // 1. Base Video Frame
                if (activeVideoClip != null) {
                    Image(
                        painter = painterResource(id = activeVideoClip.drawableResId),
                        contentDescription = activeVideoClip.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No video layer", color = TextMuted, fontSize = 12.sp)
                    }
                }

                // 2. Picture-in-Picture / Overlay Layer (if active)
                if (activeOverlayClip != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(70.dp, 90.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.5.dp, BrandMagenta, RoundedCornerShape(8.dp))
                            .shadow(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = activeOverlayClip.drawableResId),
                            contentDescription = activeOverlayClip.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color(0xCC17171D))
                                .padding(vertical = 1.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PiP AI",
                                color = BrandMagenta,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 3. Caption Overlay Rendering
                if (activeCaption != null) {
                    CaptionOverlayView(
                        caption = activeCaption,
                        currentTimestampMs = playheadPositionMs,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp, start = 12.dp, end = 12.dp)
                    )
                }

                // 4. Safe Margins Guides Overlay (TikTok / Reels Interface)
                if (safeMarginsEnabled) {
                    SafeMarginsOverlay()
                }

                // Active Filter Watermark Indicator (e.g. Cyberpunk or AI Grade)
                if (activeVideoClip?.filterName != null && activeVideoClip.filterName != "Normal") {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xAA0E0E12))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "✨ ${activeVideoClip.filterName}",
                            color = SelectionGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Transport Controls Row (Jump to Start, Step Back, Play/Pause, Step Forward)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onSeekToStart,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("preview_seek_start_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Jump to Start",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(
                onClick = { onStepFrame(false) },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("preview_step_back_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Step Back 1 Frame",
                    tint = TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Play / Pause Primary Action Button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(KlipzAccentGradient)
                    .clickable { onTogglePlayPause() }
                    .testTag("preview_play_pause_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            IconButton(
                onClick = { onStepFrame(true) },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("preview_step_forward_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Step Forward 1 Frame",
                    tint = TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun CaptionOverlayView(
    caption: CaptionItem,
    currentTimestampMs: Long,
    modifier: Modifier = Modifier
) {
    val relativeMs = currentTimestampMs - caption.startTimeMs

    when (caption.style) {
        CaptionStyle.MR_BEAST -> {
            // Bold viral karaoke style: all words shown, active word highlighted in glowing yellow
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xCC000000))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    caption.words.forEach { word ->
                        val isActive = relativeMs in word.startOffsetMs..word.endOffsetMs
                        Text(
                            text = "${word.word} ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isActive) SelectionGold else Color.White,
                            modifier = if (isActive) Modifier.padding(horizontal = 1.dp) else Modifier
                        )
                    }
                }
            }
        }
        CaptionStyle.NEON -> {
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(12.dp))
                    .background(Color(0xDD0E0E1A))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = caption.fullText,
                    color = Color(0xFF00E5FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
        else -> {
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x99000000))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = caption.fullText,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SafeMarginsOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, Color(0x6600E5FF))
    ) {
        // Top header safe zone dashed line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .background(Color(0x1A00E5FF)),
            contentAlignment = Alignment.Center
        ) {
            Text("Top Bar Safe Zone", color = Color(0x9900E5FF), fontSize = 8.sp)
        }

        // Right side TikTok action buttons zone (Like, Comment, Share, Sound)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0x66FFFFFF), modifier = Modifier.size(12.dp))
            Icon(Icons.Default.Share, contentDescription = null, tint = Color(0x66FFFFFF), modifier = Modifier.size(12.dp))
            Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color(0x66FFFFFF), modifier = Modifier.size(12.dp))
        }

        // Bottom captions zone
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(38.dp)
                .background(Color(0x1A00E5FF)),
            contentAlignment = Alignment.Center
        ) {
            Text("Bottom Caption & Sound Safe Zone", color = Color(0x9900E5FF), fontSize = 8.sp)
        }
    }
}

private fun formatTimecode(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val millis = (ms % 1000) / 10
    return String.format(Locale.US, "%02d:%02d.%02d", minutes, seconds, millis)
}
