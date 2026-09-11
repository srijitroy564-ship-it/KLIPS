package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AiJobState
import com.example.data.model.AiTaskCategory
import com.example.data.router.ModelExecutionTarget
import com.example.data.router.ModelRouterService
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiMagicDialog(
    aiJobState: AiJobState,
    onDismiss: () -> Unit,
    onRunTask: (AiTaskCategory, String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentTarget by ModelRouterService.executionTarget.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf(AiTaskCategory.AUTO_CAPTIONS) }

    val availableModels = remember(selectedCategory, currentTarget) {
        ModelRouterService.getAvailableModelsFor(selectedCategory)
    }

    var selectedModelId by remember(selectedCategory, currentTarget) {
        mutableStateOf(availableModels.firstOrNull()?.id ?: "")
    }

    var promptInput by remember(selectedCategory) {
        mutableStateOf(
            when (selectedCategory) {
                AiTaskCategory.GENERATE_BROLL -> "Cinematic drone shot of Tokyo neon rain alleyway, 4k 24fps"
                AiTaskCategory.FLUX_IMAGEN_IMAGE -> "Photorealistic fantasy landscape with glowing violet aurora"
                AiTaskCategory.VOICE_CLONING -> "Welcome back creators, today we are building something truly special."
                AiTaskCategory.MOVIE_COLOR_GRADE -> "Oppenheimer 35mm IMAX Film LUT"
                AiTaskCategory.FACE_BODY_RELIGHTING -> "Cyberpunk Neon & Gold Rim Lighting"
                AiTaskCategory.KINETIC_TEXT_VFX -> "Marvel Cinematic 3D Glass & Fire Dust"
                AiTaskCategory.FILM_GRAIN_ARTIFACTS -> "Kodak 5219 35mm Grain + Vintage Light Leaks"
                AiTaskCategory.SCRIPT_STORYBOARD_GEN -> "Top 5 AI Video Editing Tools for Creators"
                AiTaskCategory.BRANDING_BUMPER_KIT -> "Klipz Creator Studio"
                else -> ""
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        dragHandle = null
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Title, Logo & Close
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(KlipzPrimaryGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AI Magic & Model Router",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Pluggable Local/On-Device NPU & Cloud Endpoints",
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
            }

            // Model Router Execution Target Switcher (Local NPU vs Cloud vs Hybrid)
            item {
                Text(
                    text = "EXECUTION TARGET / ROUTER MODE",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCard)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ModelExecutionTarget.entries.forEach { target ->
                        val isSelected = target == currentTarget
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) ElectricIndigo else Color.Transparent)
                                .clickable {
                                    ModelRouterService.setExecutionTarget(target)
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                when (target) {
                                    ModelExecutionTarget.LOCAL_ON_DEVICE -> Icon(Icons.Default.Bolt, contentDescription = null, tint = if (isSelected) Color.White else SelectionGold, modifier = Modifier.size(14.dp))
                                    ModelExecutionTarget.CLOUD_API -> Icon(Icons.Default.Cloud, contentDescription = null, tint = if (isSelected) Color.White else TextSecondary, modifier = Modifier.size(14.dp))
                                    ModelExecutionTarget.AUTO_HYBRID -> Icon(Icons.Default.Sync, contentDescription = null, tint = if (isSelected) Color.White else Color(0xFF1CB586), modifier = Modifier.size(14.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = target.badge,
                                    color = if (isSelected) Color.White else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currentTarget.description,
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // AI Task Category Horizontal Tabs
            item {
                Text(
                    text = "SELECT AI FEATURE",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AiTaskCategory.entries) { category ->
                        val isSelected = category == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) ElectricIndigo else DarkSurfaceElevated)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.3f) else DarkBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedCategory = category
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("ai_category_tab_${category.name}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = category.icon, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = category.title,
                                    color = if (isSelected) Color.White else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Category Description Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkCard)
                        .padding(12.dp)
                ) {
                    Text(
                        text = selectedCategory.description,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Prompt / Input field if category supports custom input
            item {
                val needsInput = selectedCategory in listOf(
                    AiTaskCategory.GENERATE_BROLL,
                    AiTaskCategory.FLUX_IMAGEN_IMAGE,
                    AiTaskCategory.VOICE_CLONING,
                    AiTaskCategory.MOVIE_COLOR_GRADE,
                    AiTaskCategory.FACE_BODY_RELIGHTING,
                    AiTaskCategory.KINETIC_TEXT_VFX,
                    AiTaskCategory.FILM_GRAIN_ARTIFACTS,
                    AiTaskCategory.SCRIPT_STORYBOARD_GEN,
                    AiTaskCategory.BRANDING_BUMPER_KIT,
                    AiTaskCategory.REMOVE_BACKGROUND
                )

                if (needsInput) {
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        label = {
                            Text(
                                text = when (selectedCategory) {
                                    AiTaskCategory.GENERATE_BROLL -> "Video Prompt (Veo 3.1)"
                                    AiTaskCategory.FLUX_IMAGEN_IMAGE -> "Flux.1 / Imagen 3 Prompt"
                                    AiTaskCategory.VOICE_CLONING -> "Speech Script to Synthesize"
                                    AiTaskCategory.MOVIE_COLOR_GRADE -> "Movie Frame Screenshot / LUT Name"
                                    AiTaskCategory.FACE_BODY_RELIGHTING -> "Lighting Preset (e.g., Cyberpunk Neon, Golden Hour)"
                                    AiTaskCategory.KINETIC_TEXT_VFX -> "Motion Graphics Style (Marvel, Fire Dust, 3D Glass)"
                                    AiTaskCategory.FILM_GRAIN_ARTIFACTS -> "Film Stock & Grain (16mm, 35mm, Light Leaks)"
                                    AiTaskCategory.SCRIPT_STORYBOARD_GEN -> "Video Topic for Script Generator"
                                    AiTaskCategory.BRANDING_BUMPER_KIT -> "Creator / Brand Name"
                                    AiTaskCategory.REMOVE_BACKGROUND -> "New Background Prompt (Flow AI)"
                                    else -> "Custom Parameters"
                                },
                                fontSize = 11.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_prompt_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricIndigo,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = DarkBg,
                            unfocusedContainerColor = DarkBg
                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = false,
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // Available Models List
            item {
                Text(
                    text = "SELECT AI MODEL / ENDPOINT",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(availableModels) { model ->
                val isModelSelected = model.id == selectedModelId
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isModelSelected) Color(0x335B4CFF) else DarkSurfaceElevated)
                        .border(
                            width = 1.dp,
                            color = if (isModelSelected) ElectricIndigo else DarkBorder,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedModelId = model.id }
                        .padding(12.dp)
                        .testTag("ai_model_card_${model.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = model.name,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (model.isLocalOnDevice) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0x331CB586))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("⚡ NPU", color = Color(0xFF1CB586), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (model.isRecommended) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF2B3AD8))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("TOP PICK", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(
                            text = "${model.provider} • Est. Latency: ${model.latencyEstimate}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    // Tier badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (model.tier.contains("Credit")) Color(0x33FFD15C)
                                else Color(0x331CB586)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = model.tier,
                            color = if (model.tier.contains("Credit")) SelectionGold else Color(0xFF1CB586),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Job Status & Progress
            item {
                Spacer(modifier = Modifier.height(10.dp))
                when (aiJobState) {
                    is AiJobState.Processing -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkCard)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = ElectricIndigo,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = aiJobState.statusMessage,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { aiJobState.progress },
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                color = ElectricIndigo,
                                trackColor = DarkBorder
                            )
                        }
                    }
                    is AiJobState.Success -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x221CB586))
                                .border(1.dp, Color(0xFF1CB586), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF1CB586), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = aiJobState.message, color = Color(0xFF1CB586), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    is AiJobState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x22FF4D4D))
                                .border(1.dp, Color(0xFFFF4D4D), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(text = "Error: ${aiJobState.errorMessage}", color = Color(0xFFFF4D4D), fontSize = 11.sp)
                        }
                    }
                    else -> {}
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Execute Button
                Button(
                    onClick = {
                        onRunTask(selectedCategory, selectedModelId, promptInput)
                    },
                    enabled = aiJobState !is AiJobState.Processing && selectedModelId.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("ai_run_task_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Run ${availableModels.find { it.id == selectedModelId }?.name ?: "Model"}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
