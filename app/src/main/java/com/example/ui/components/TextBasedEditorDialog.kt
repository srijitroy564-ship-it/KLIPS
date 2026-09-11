package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.KlipzPrimaryGradient
import com.example.ui.theme.SelectionGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class TranscriptWordItem(
    val id: String,
    val word: String,
    val isPause: Boolean = false,
    val durationMs: Long = 400L
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TextBasedEditorDialog(
    onDismiss: () -> Unit,
    onApplyTrim: (removedWordCount: Int, removedDurationMs: Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val initialWords = remember {
        listOf(
            TranscriptWordItem("w1", "Hey"),
            TranscriptWordItem("w2", "creators!"),
            TranscriptWordItem("p1", "[...0.8s silence...]", isPause = true, durationMs = 800L),
            TranscriptWordItem("w3", "Today"),
            TranscriptWordItem("w4", "we're"),
            TranscriptWordItem("w5", "building"),
            TranscriptWordItem("w6", "the"),
            TranscriptWordItem("w7", "fastest"),
            TranscriptWordItem("w8", "AI"),
            TranscriptWordItem("w9", "video"),
            TranscriptWordItem("w10", "editor"),
            TranscriptWordItem("p2", "[...1.2s umm/breath...]", isPause = true, durationMs = 1200L),
            TranscriptWordItem("w11", "with"),
            TranscriptWordItem("w12", "zero"),
            TranscriptWordItem("w13", "watermarks"),
            TranscriptWordItem("w14", "and"),
            TranscriptWordItem("w15", "local"),
            TranscriptWordItem("w16", "NPU"),
            TranscriptWordItem("w17", "speed."),
            TranscriptWordItem("p3", "[...0.6s pause...]", isPause = true, durationMs = 600L),
            TranscriptWordItem("w18", "Hit"),
            TranscriptWordItem("w19", "subscribe"),
            TranscriptWordItem("w20", "now!")
        )
    }

    val deletedWordIds = remember { mutableStateListOf<String>() }

    val totalRemovedMs = remember(deletedWordIds.size) {
        initialWords.filter { it.id in deletedWordIds }.sumOf { it.durationMs }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(KlipzPrimaryGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TextFields,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Text-Based Video Editing",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Descript-Style: Delete transcript words to auto-trim video",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Actions: Auto-Select Silences & Reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        // Mark all silence/pause items as deleted
                        initialWords.filter { it.isPause }.forEach { pauseItem ->
                            if (pauseItem.id !in deletedWordIds) {
                                deletedWordIds.add(pauseItem.id)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FF4D4D)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.ContentCut, contentDescription = null, tint = Color(0xFFFF4D4D), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Auto-Cut All Silences", color = Color(0xFFFF4D4D), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { deletedWordIds.clear() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                ) {
                    Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Instructions Callout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkCard)
                    .padding(10.dp)
            ) {
                Text(
                    text = "Tap on any word or silence pause below to cross it out. When applied, the video timeline automatically compacts and splices out those exact frames.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Word Flow Grid
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkBg)
            ) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    initialWords.forEach { item ->
                        val isDeleted = item.id in deletedWordIds
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when {
                                        isDeleted -> Color(0x33FF4D4D)
                                        item.isPause -> Color(0x33FFD15C)
                                        else -> DarkSurfaceElevated
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = when {
                                        isDeleted -> Color(0xFFFF4D4D)
                                        item.isPause -> SelectionGold
                                        else -> DarkBorder
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    if (isDeleted) {
                                        deletedWordIds.remove(item.id)
                                    } else {
                                        deletedWordIds.add(item.id)
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = item.word,
                                color = when {
                                    isDeleted -> Color(0xFFFF4D4D)
                                    item.isPause -> SelectionGold
                                    else -> TextPrimary
                                },
                                fontSize = 13.sp,
                                textDecoration = if (isDeleted) TextDecoration.LineThrough else TextDecoration.None,
                                fontWeight = if (item.isPause) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${deletedWordIds.size} segments marked for removal",
                    color = if (deletedWordIds.isNotEmpty()) Color(0xFFFF4D4D) else TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "-${totalRemovedMs / 1000f}s cut from timeline",
                    color = SelectionGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Apply Button
            Button(
                onClick = {
                    onApplyTrim(deletedWordIds.size, totalRemovedMs)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("apply_transcript_trim_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (deletedWordIds.isEmpty()) "Keep Full Video" else "Trim Video to Edited Transcript (${totalRemovedMs / 1000f}s shorter)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
