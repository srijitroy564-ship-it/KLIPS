package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.auth.CoinTransaction
import com.example.data.auth.CreditsManager
import com.example.data.auth.GoogleAuthManager
import com.example.data.auth.UserProfile
import com.example.data.model.AiJobState
import com.example.data.model.AiTaskCategory
import com.example.data.model.AiTaskRequest
import com.example.data.model.AspectRatio
import com.example.data.model.CaptionItem
import com.example.data.model.CaptionStyle
import com.example.data.model.CaptionWord
import com.example.data.model.Project
import com.example.data.model.TimelineClip
import com.example.data.model.TimelineTrack
import com.example.data.model.TrackType
import com.example.data.model.TransitionType
import com.example.data.router.ModelRouterService
import com.example.data.router.SampleProjects
import com.example.data.router.StoryboardData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditorViewModel(application: Application) : AndroidViewModel(application) {

    val creditsManager = CreditsManager(application.applicationContext)
    val authManager = GoogleAuthManager(application.applicationContext, creditsManager)

    val userCredits: StateFlow<Int> = creditsManager.coins
    val userProfile: StateFlow<UserProfile?> = creditsManager.userProfile
    val transactions: StateFlow<List<CoinTransaction>> = creditsManager.transactions

    private val _project = MutableStateFlow(SampleProjects.createDefaultProject())
    val project: StateFlow<Project> = _project.asStateFlow()

    private val _playheadPositionMs = MutableStateFlow(2400L)
    val playheadPositionMs: StateFlow<Long> = _playheadPositionMs.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _zoomScale = MutableStateFlow(1.0f)
    val zoomScale: StateFlow<Float> = _zoomScale.asStateFlow()

    private val _selectedClipId = MutableStateFlow<String?>("clip_skate")
    val selectedClipId: StateFlow<String?> = _selectedClipId.asStateFlow()

    private val _safeMarginsEnabled = MutableStateFlow(false)
    val safeMarginsEnabled: StateFlow<Boolean> = _safeMarginsEnabled.asStateFlow()

    private val _aiJobState = MutableStateFlow<AiJobState>(AiJobState.Idle)
    val aiJobState: StateFlow<AiJobState> = _aiJobState.asStateFlow()

    private val undoStack = mutableListOf<Project>()
    private val redoStack = mutableListOf<Project>()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    private var playbackJob: Job? = null

    init {
        // Automatically award 50 default coins if signed in for first time
        if (userProfile.value == null) {
            authManager.signInWithDemoAccount()
        }
    }

    fun signInDemo() {
        authManager.signInWithDemoAccount()
    }

    fun signOut() {
        authManager.signOut()
    }

    fun loadProject(newProject: Project) {
        pause()
        _project.value = newProject
        _playheadPositionMs.value = 0L
        _selectedClipId.value = newProject.tracks.firstOrNull { it.type == TrackType.VIDEO }
            ?.clips?.firstOrNull()?.id
        undoStack.clear()
        redoStack.clear()
        updateUndoRedoStates()
    }

    fun seekTo(positionMs: Long) {
        val total = _project.value.totalDurationMs
        _playheadPositionMs.value = positionMs.coerceIn(0L, total)
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }

    private fun play() {
        _isPlaying.value = true
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val stepMs = 33L
            while (_isPlaying.value) {
                delay(stepMs)
                val current = _playheadPositionMs.value
                val total = _project.value.totalDurationMs
                if (current >= total) {
                    _playheadPositionMs.value = 0L
                } else {
                    _playheadPositionMs.value = (current + stepMs).coerceAtMost(total)
                }
            }
        }
    }

    fun pause() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
    }

    fun stepFrame(forward: Boolean) {
        pause()
        val delta = 1000L / _project.value.fps
        val target = if (forward) _playheadPositionMs.value + delta else _playheadPositionMs.value - delta
        seekTo(target)
    }

    fun selectClip(clipId: String?) {
        _selectedClipId.value = clipId
    }

    fun setZoomScale(scale: Float) {
        _zoomScale.value = scale.coerceIn(0.4f, 3.0f)
    }

    fun toggleSafeMargins() {
        _safeMarginsEnabled.value = !_safeMarginsEnabled.value
    }

    fun setAspectRatio(newRatio: AspectRatio) {
        saveStateForUndo()
        _project.value = _project.value.copy(aspectRatio = newRatio)
    }

    fun splitClipAtPlayhead() {
        val currentPlayhead = _playheadPositionMs.value
        val currentSelectedId = _selectedClipId.value ?: return
        val currentProject = _project.value

        var targetClip: TimelineClip? = null
        var targetTrack: TimelineTrack? = null

        for (track in currentProject.tracks) {
            val found = track.clips.find { it.id == currentSelectedId }
            if (found != null) {
                targetClip = found
                targetTrack = track
                break
            }
        }

        if (targetClip == null || targetTrack == null) return

        val clipStart = targetClip.startTimeMs
        val clipEnd = clipStart + targetClip.durationMs

        if (currentPlayhead <= clipStart + 200L || currentPlayhead >= clipEnd - 200L) {
            return
        }

        saveStateForUndo()

        val firstPartDuration = currentPlayhead - clipStart
        val secondPartDuration = clipEnd - currentPlayhead

        val firstClip = targetClip.copy(
            id = "${targetClip.id}_part1",
            durationMs = firstPartDuration
        )

        val secondClip = targetClip.copy(
            id = "${targetClip.id}_part2",
            startTimeMs = currentPlayhead,
            durationMs = secondPartDuration,
            transitionIn = TransitionType.NONE
        )

        val updatedClips = mutableListOf<TimelineClip>()
        for (c in targetTrack.clips) {
            if (c.id == targetClip.id) {
                updatedClips.add(firstClip)
                updatedClips.add(secondClip)
            } else {
                updatedClips.add(c)
            }
        }

        val updatedTrack = targetTrack.copy(clips = updatedClips)
        val updatedTracks = currentProject.tracks.map {
            if (it.id == updatedTrack.id) updatedTrack else it
        }

        _project.value = currentProject.copy(tracks = updatedTracks)
        _selectedClipId.value = secondClip.id
    }

    fun deleteSelectedClip() {
        val selectedId = _selectedClipId.value ?: return
        saveStateForUndo()

        val currentProject = _project.value
        val updatedTracks = currentProject.tracks.map { track ->
            track.copy(clips = track.clips.filterNot { it.id == selectedId })
        }

        _project.value = currentProject.copy(tracks = updatedTracks)
        _selectedClipId.value = null
    }

    fun duplicateSelectedClip() {
        val selectedId = _selectedClipId.value ?: return
        val currentProject = _project.value
        val (targetTrack, targetClip) = findTrackAndClip(selectedId) ?: return

        saveStateForUndo()

        val newClip = targetClip.copy(
            id = "clip_dup_${System.currentTimeMillis()}",
            startTimeMs = targetClip.startTimeMs + targetClip.durationMs,
            title = "${targetClip.title} (Copy)"
        )

        val updatedClips = targetTrack.clips.toMutableList().apply { add(newClip) }
        val updatedTrack = targetTrack.copy(clips = updatedClips)
        val updatedTracks = currentProject.tracks.map { if (it.id == updatedTrack.id) updatedTrack else it }

        _project.value = currentProject.copy(tracks = updatedTracks)
        _selectedClipId.value = newClip.id
    }

    fun setClipSpeed(speed: Float) {
        val selectedId = _selectedClipId.value ?: return
        val (targetTrack, targetClip) = findTrackAndClip(selectedId) ?: return

        saveStateForUndo()
        val newDuration = (targetClip.sourceDurationMs / speed).toLong()
        val updatedClip = targetClip.copy(speed = speed, durationMs = newDuration)

        updateClipInProject(targetTrack.id, updatedClip)
    }

    fun setClipTransition(transition: TransitionType) {
        val selectedId = _selectedClipId.value ?: return
        val (targetTrack, targetClip) = findTrackAndClip(selectedId) ?: return

        saveStateForUndo()
        val updatedClip = targetClip.copy(transitionIn = transition)
        updateClipInProject(targetTrack.id, updatedClip)
    }

    fun setClipFilter(filterName: String) {
        val selectedId = _selectedClipId.value ?: return
        val (targetTrack, targetClip) = findTrackAndClip(selectedId) ?: return

        saveStateForUndo()
        val updatedClip = targetClip.copy(filterName = filterName)
        updateClipInProject(targetTrack.id, updatedClip)
    }

    /**
     * Applies Descript-Style text-based transcript edit to compact the timeline.
     */
    fun applyTranscriptTrim(removedWordCount: Int, removedDurationMs: Long) {
        if (removedDurationMs <= 0L) return
        saveStateForUndo()

        val current = _project.value
        val videoTrack = current.tracks.find { it.type == TrackType.VIDEO } ?: return
        if (videoTrack.clips.isEmpty()) return

        var runningStart = 0L
        val updatedClips = videoTrack.clips.mapIndexed { idx, clip ->
            val trimAmount = if (idx == 0) removedDurationMs.coerceAtMost(clip.durationMs - 500L) else 0L
            val newDuration = clip.durationMs - trimAmount
            val modified = clip.copy(
                startTimeMs = runningStart,
                durationMs = newDuration
            )
            runningStart += newDuration
            modified
        }

        val updatedTracks = current.tracks.map { track ->
            if (track.type == TrackType.VIDEO) track.copy(clips = updatedClips) else track
        }

        _project.value = current.copy(tracks = updatedTracks)
        _aiJobState.value = AiJobState.Success(
            message = "Descript Edit: Trimmed $removedWordCount words (-${removedDurationMs / 1000f}s). Timeline compacted!"
        )
    }

    /**
     * Applies an AI generated Storyboard with scene clips & captions.
     */
    fun applyStoryboard(storyboardData: StoryboardData) {
        saveStateForUndo()

        var runningTime = 0L
        val sceneClips = storyboardData.scenes.map { scene ->
            val durationMs = scene.durationSec * 1000L
            val clip = TimelineClip(
                id = "scene_${scene.sceneNumber}_${System.currentTimeMillis()}",
                title = scene.title,
                drawableResId = R.drawable.sample_astronaut,
                startTimeMs = runningTime,
                durationMs = durationMs,
                sourceDurationMs = durationMs,
                isAiGenerated = true,
                filterName = "Cinematic ${scene.shotType.take(8)}"
            )
            runningTime += durationMs
            clip
        }

        var capStart = 0L
        val captions = storyboardData.scenes.map { scene ->
            val durationMs = scene.durationSec * 1000L
            val item = CaptionItem(
                id = "sb_cap_${scene.sceneNumber}",
                startTimeMs = capStart,
                durationMs = durationMs,
                fullText = scene.narration.uppercase(),
                style = CaptionStyle.MR_BEAST,
                words = scene.narration.split(" ").mapIndexed { wIdx, word ->
                    val wStart = capStart + (wIdx * 300L)
                    CaptionWord(word.uppercase(), wStart, wStart + 280L)
                }
            )
            capStart += durationMs
            item
        }

        val videoTrack = TimelineTrack(
            id = "track_sb_video",
            type = TrackType.VIDEO,
            name = "Storyboard Scenes",
            clips = sceneClips
        )

        val textTrack = TimelineTrack(
            id = "track_sb_text",
            type = TrackType.TEXT,
            name = "Script Captions",
            captions = captions
        )

        val newProject = Project(
            id = "proj_sb_${System.currentTimeMillis()}",
            name = storyboardData.topic,
            aspectRatio = AspectRatio.RATIO_9_16,
            tracks = listOf(videoTrack, textTrack)
        )

        loadProject(newProject)
        _aiJobState.value = AiJobState.Success(
            message = "Applied shooting script & shot list: ${storyboardData.scenes.size} scenes ready in 9:16 format!"
        )
    }

    fun runAiTask(category: AiTaskCategory, modelId: String, prompt: String) {
        viewModelScope.launch {
            _aiJobState.value = AiJobState.Processing(0.1f, "Initializing $category...")
            try {
                val request = AiTaskRequest(
                    taskCategory = category,
                    selectedModelId = modelId,
                    prompt = prompt,
                    targetClipId = _selectedClipId.value
                )

                val result = ModelRouterService.executeTask(request) { progress, status ->
                    _aiJobState.value = AiJobState.Processing(progress, status)
                }

                if (result.creditsDeducted > 0) {
                    creditsManager.deductCoins(result.creditsDeducted, "AI: ${category.title}")
                }

                saveStateForUndo()

                val current = _project.value
                var newTracks = current.tracks

                if (result.captions.isNotEmpty()) {
                    newTracks = newTracks.map { track ->
                        if (track.type == TrackType.TEXT) {
                            track.copy(captions = track.captions + result.captions)
                        } else track
                    }
                }

                if (result.clips.isNotEmpty()) {
                    newTracks = newTracks.map { track ->
                        if (track.type == TrackType.VIDEO) {
                            track.copy(clips = track.clips + result.clips)
                        } else if (track.type == TrackType.AUDIO && (category == AiTaskCategory.VOICE_CLONING || category == AiTaskCategory.AI_MUSIC_GEN || category == AiTaskCategory.VOICE_CLEANER_STUDIO)) {
                            track.copy(clips = track.clips + result.clips)
                        } else track
                    }
                }

                if (result.modifiedFilter != null) {
                    val selId = _selectedClipId.value
                    if (selId != null) {
                        val (tTrack, tClip) = findTrackAndClip(selId) ?: (null to null)
                        if (tTrack != null && tClip != null) {
                            val modClip = tClip.copy(
                                filterName = result.modifiedFilter,
                                hasRemovedBackground = category == AiTaskCategory.REMOVE_BACKGROUND
                            )
                            newTracks = newTracks.map { trk ->
                                if (trk.id == tTrack.id) {
                                    trk.copy(clips = trk.clips.map { if (it.id == selId) modClip else it })
                                } else trk
                            }
                        }
                    }
                }

                if (category == AiTaskCategory.SMART_AUTO_REFRAME) {
                    _project.value = current.copy(aspectRatio = AspectRatio.RATIO_9_16, tracks = newTracks)
                } else {
                    _project.value = current.copy(tracks = newTracks)
                }

                _aiJobState.value = AiJobState.Success(message = result.message)

            } catch (e: Exception) {
                _aiJobState.value = AiJobState.Error(e.message ?: "AI Task failed")
            }
        }
    }

    fun clearAiJob() {
        _aiJobState.value = AiJobState.Idle
    }

    private fun findTrackAndClip(clipId: String): Pair<TimelineTrack, TimelineClip>? {
        for (track in _project.value.tracks) {
            val clip = track.clips.find { it.id == clipId }
            if (clip != null) return track to clip
        }
        return null
    }

    private fun updateClipInProject(trackId: String, updatedClip: TimelineClip) {
        val updatedTracks = _project.value.tracks.map { track ->
            if (track.id == trackId) {
                track.copy(clips = track.clips.map { if (it.id == updatedClip.id) updatedClip else it })
            } else track
        }
        _project.value = _project.value.copy(tracks = updatedTracks)
    }

    private fun saveStateForUndo() {
        undoStack.add(_project.value)
        redoStack.clear()
        updateUndoRedoStates()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val previous = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(_project.value)
            _project.value = previous
            updateUndoRedoStates()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(_project.value)
            _project.value = next
            updateUndoRedoStates()
        }
    }

    private fun updateUndoRedoStates() {
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = redoStack.isNotEmpty()
    }
}
