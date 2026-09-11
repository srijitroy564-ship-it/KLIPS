package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AiJobState
import com.example.data.model.TransitionType
import com.example.ui.components.AiMagicDialog
import com.example.ui.components.EditorToolbar
import com.example.ui.components.EditorTopBar
import com.example.ui.components.ExportDialog
import com.example.ui.components.FiltersDialog
import com.example.ui.components.GoogleSignInDialog
import com.example.ui.components.PreviewViewport
import com.example.ui.components.SpeedDialog
import com.example.ui.components.StoryboardDialog
import com.example.ui.components.TextBasedEditorDialog
import com.example.ui.components.TimelineView
import com.example.ui.components.ToolbarAction
import com.example.ui.components.TransitionsDialog
import com.example.ui.theme.DarkBg
import com.example.ui.viewmodel.EditorViewModel
import kotlinx.coroutines.launch

@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val project by viewModel.project.collectAsStateWithLifecycle()
    val playheadMs by viewModel.playheadPositionMs.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val zoomScale by viewModel.zoomScale.collectAsStateWithLifecycle()
    val selectedClipId by viewModel.selectedClipId.collectAsStateWithLifecycle()
    val safeMarginsEnabled by viewModel.safeMarginsEnabled.collectAsStateWithLifecycle()
    val credits by viewModel.userCredits.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val aiJobState by viewModel.aiJobState.collectAsStateWithLifecycle()
    val canUndo by viewModel.canUndo.collectAsStateWithLifecycle()
    val canRedo by viewModel.canRedo.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showAiMagicDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }
    var showTransitionsDialog by remember { mutableStateOf(false) }
    var showFiltersDialog by remember { mutableStateOf(false) }
    var showGoogleSignInDialog by remember { mutableStateOf(false) }
    var showTextBasedEditorDialog by remember { mutableStateOf(false) }
    var showStoryboardDialog by remember { mutableStateOf(false) }

    // Notify user on AI completion
    LaunchedEffect(aiJobState) {
        if (aiJobState is AiJobState.Success) {
            snackbarHostState.showSnackbar((aiJobState as AiJobState.Success).message)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBg,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBg)
        ) {
            // 1. Top Bar with Account & Credits dialog opener
            EditorTopBar(
                projectName = project.name,
                canUndo = canUndo,
                canRedo = canRedo,
                credits = credits,
                onBackClick = onNavigateBack,
                onUndoClick = { viewModel.undo() },
                onRedoClick = { viewModel.redo() },
                onExportClick = { showExportDialog = true },
                onAccountClick = { showGoogleSignInDialog = true }
            )

            // 2. Preview Viewport (Canvas, Aspect Ratio, Live Frames, Safe Guides, Transport)
            PreviewViewport(
                project = project,
                playheadPositionMs = playheadMs,
                isPlaying = isPlaying,
                safeMarginsEnabled = safeMarginsEnabled,
                onTogglePlayPause = { viewModel.togglePlayPause() },
                onStepFrame = { viewModel.stepFrame(it) },
                onSeekToStart = { viewModel.seekTo(0L) },
                onToggleSafeMargins = { viewModel.toggleSafeMargins() },
                onSelectAspectRatio = { viewModel.setAspectRatio(it) }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Non-Destructive Multi-Track Timeline (Ruler, Playhead, Video/Audio/Text Tracks)
            TimelineView(
                project = project,
                playheadPositionMs = playheadMs,
                zoomScale = zoomScale,
                selectedClipId = selectedClipId,
                onSeekTo = { viewModel.seekTo(it) },
                onSelectClip = { viewModel.selectClip(it) },
                onZoomChange = { viewModel.setZoomScale(it) },
                modifier = Modifier.weight(1f)
            )

            // 4. Bottom Action Toolbar
            EditorToolbar(
                hasSelectedClip = selectedClipId != null,
                onActionClick = { action ->
                    when (action) {
                        ToolbarAction.AI_MAGIC -> showAiMagicDialog = true
                        ToolbarAction.TEXT_EDIT -> showTextBasedEditorDialog = true
                        ToolbarAction.STORYBOARD -> showStoryboardDialog = true
                        ToolbarAction.SPLIT -> {
                            viewModel.splitClipAtPlayhead()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Clip split at playhead!")
                            }
                        }
                        ToolbarAction.SPEED -> showSpeedDialog = true
                        ToolbarAction.TRANSITIONS -> showTransitionsDialog = true
                        ToolbarAction.FILTERS -> showFiltersDialog = true
                        ToolbarAction.DUPLICATE -> {
                            viewModel.duplicateSelectedClip()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Clip duplicated on timeline.")
                            }
                        }
                        ToolbarAction.DELETE -> {
                            viewModel.deleteSelectedClip()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Clip deleted.")
                            }
                        }
                        ToolbarAction.AUDIO -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Audio track gain normalized at 0dB.")
                            }
                        }
                        ToolbarAction.TEXT -> {
                            showAiMagicDialog = true
                        }
                        ToolbarAction.OVERLAY -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("PiP Overlay active. Use gesture to reposition.")
                            }
                        }
                    }
                }
            )
        }

        // Modals & Bottom Sheets
        if (showAiMagicDialog) {
            AiMagicDialog(
                aiJobState = aiJobState,
                onDismiss = {
                    showAiMagicDialog = false
                    viewModel.clearAiJob()
                },
                onRunTask = { category, modelId, prompt ->
                    viewModel.runAiTask(category, modelId, prompt)
                }
            )
        }

        if (showGoogleSignInDialog) {
            GoogleSignInDialog(
                userProfile = userProfile,
                coins = credits,
                transactions = transactions,
                onDismiss = { showGoogleSignInDialog = false },
                onSignInDemo = {
                    viewModel.signInDemo()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Signed in with Google! 50 Free Coins active.")
                    }
                },
                onSignOut = {
                    viewModel.signOut()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Signed out.")
                    }
                }
            )
        }

        if (showTextBasedEditorDialog) {
            TextBasedEditorDialog(
                onDismiss = { showTextBasedEditorDialog = false },
                onApplyTrim = { wordCount, durationMs ->
                    viewModel.applyTranscriptTrim(wordCount, durationMs)
                }
            )
        }

        if (showStoryboardDialog) {
            StoryboardDialog(
                onDismiss = { showStoryboardDialog = false },
                onApplyStoryboardToTimeline = { storyboardData ->
                    viewModel.applyStoryboard(storyboardData)
                }
            )
        }

        if (showExportDialog) {
            ExportDialog(
                projectName = project.name,
                onDismiss = { showExportDialog = false }
            )
        }

        if (showSpeedDialog) {
            SpeedDialog(
                currentSpeed = 1.0f,
                onSpeedSelected = { viewModel.setClipSpeed(it) },
                onDismiss = { showSpeedDialog = false }
            )
        }

        if (showTransitionsDialog) {
            TransitionsDialog(
                currentTransition = TransitionType.NONE,
                onTransitionSelected = { viewModel.setClipTransition(it) },
                onDismiss = { showTransitionsDialog = false }
            )
        }

        if (showFiltersDialog) {
            FiltersDialog(
                currentFilter = "Normal",
                onFilterSelected = { viewModel.setClipFilter(it) },
                onDismiss = { showFiltersDialog = false }
            )
        }
    }
}
