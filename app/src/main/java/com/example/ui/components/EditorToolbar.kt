package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.KlipzPrimaryGradient
import com.example.ui.theme.SelectionGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class ToolbarAction {
    SPLIT,
    SPEED,
    TRANSITIONS,
    FILTERS,
    AUDIO,
    AI_MAGIC,
    TEXT_EDIT,
    STORYBOARD,
    TEXT,
    OVERLAY,
    DUPLICATE,
    DELETE
}

@Composable
fun EditorToolbar(
    hasSelectedClip: Boolean,
    onActionClick: (ToolbarAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(DarkSurface)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .horizontalScroll(scrollState),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // AI Magic Tools (Special Brand Highlight Action)
        ToolbarActionButton(
            label = "AI Magic",
            icon = Icons.Default.AutoAwesome,
            isSpecialAi = true,
            onClick = { onActionClick(ToolbarAction.AI_MAGIC) },
            testTag = "toolbar_btn_ai_magic"
        )

        // Text-Based Edit (Descript Style)
        ToolbarActionButton(
            label = "Text Cut",
            icon = Icons.Default.TextFields,
            onClick = { onActionClick(ToolbarAction.TEXT_EDIT) },
            testTag = "toolbar_btn_text_edit"
        )

        // AI Script & Storyboard
        ToolbarActionButton(
            label = "Storyboard",
            icon = Icons.Default.Description,
            onClick = { onActionClick(ToolbarAction.STORYBOARD) },
            testTag = "toolbar_btn_storyboard"
        )

        // Split
        ToolbarActionButton(
            label = "Split",
            icon = Icons.Default.ContentCut,
            isEnabled = hasSelectedClip,
            onClick = { onActionClick(ToolbarAction.SPLIT) },
            testTag = "toolbar_btn_split"
        )

        // Speed
        ToolbarActionButton(
            label = "Speed",
            icon = Icons.Default.Speed,
            isEnabled = hasSelectedClip,
            onClick = { onActionClick(ToolbarAction.SPEED) },
            testTag = "toolbar_btn_speed"
        )

        // Transitions
        ToolbarActionButton(
            label = "Transitions",
            icon = Icons.Default.Transform,
            isEnabled = hasSelectedClip,
            onClick = { onActionClick(ToolbarAction.TRANSITIONS) },
            testTag = "toolbar_btn_transitions"
        )

        // Filters
        ToolbarActionButton(
            label = "Filters",
            icon = Icons.Default.FilterVintage,
            isEnabled = hasSelectedClip,
            onClick = { onActionClick(ToolbarAction.FILTERS) },
            testTag = "toolbar_btn_filters"
        )

        // Audio
        ToolbarActionButton(
            label = "Audio",
            icon = Icons.AutoMirrored.Filled.VolumeUp,
            onClick = { onActionClick(ToolbarAction.AUDIO) },
            testTag = "toolbar_btn_audio"
        )

        // Text & Captions
        ToolbarActionButton(
            label = "Text",
            icon = Icons.Default.TextFields,
            onClick = { onActionClick(ToolbarAction.TEXT) },
            testTag = "toolbar_btn_text"
        )

        // Overlay / PiP
        ToolbarActionButton(
            label = "Overlay",
            icon = Icons.Default.Layers,
            onClick = { onActionClick(ToolbarAction.OVERLAY) },
            testTag = "toolbar_btn_overlay"
        )

        // Duplicate
        if (hasSelectedClip) {
            ToolbarActionButton(
                label = "Duplicate",
                icon = Icons.Default.ContentCopy,
                onClick = { onActionClick(ToolbarAction.DUPLICATE) },
                testTag = "toolbar_btn_duplicate"
            )

            ToolbarActionButton(
                label = "Delete",
                icon = Icons.Default.Delete,
                isDestructive = true,
                onClick = { onActionClick(ToolbarAction.DELETE) },
                testTag = "toolbar_btn_delete"
            )
        }
    }
}

@Composable
private fun ToolbarActionButton(
    label: String,
    icon: ImageVector,
    isEnabled: Boolean = true,
    isSpecialAi: Boolean = false,
    isDestructive: Boolean = false,
    onClick: () -> Unit,
    testTag: String
) {
    val contentColor = when {
        !isEnabled -> TextMuted
        isDestructive -> Color(0xFFFF4D4D)
        isSpecialAi -> Color.White
        else -> TextPrimary
    }

    Column(
        modifier = Modifier
            .width(54.dp)
            .clickable(enabled = isEnabled) { onClick() }
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isSpecialAi) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(KlipzPrimaryGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isEnabled) DarkSurfaceElevated else DarkSurface)
                    .border(
                        width = 1.dp,
                        color = if (isEnabled) DarkBorder else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            color = contentColor,
            fontSize = 10.sp,
            fontWeight = if (isSpecialAi) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
    }
}
