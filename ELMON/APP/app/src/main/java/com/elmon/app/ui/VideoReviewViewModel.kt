package com.elmon.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elmon.app.data.model.VideoItem
import com.elmon.app.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VideoReviewViewModel(
    private val repository: VideoRepository
) : ViewModel() {
    private val _state = MutableStateFlow(VideoFeedState(isLoading = true))
    val state: StateFlow<VideoFeedState> = _state.asStateFlow()

    private var allVideos: List<VideoItem> = emptyList()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            loadPendingVideos()
        }
    }

    fun rateVideo(video: VideoItem, liked: Boolean, feedback: String?) {
        viewModelScope.launch {
            try {
                repository.rateVideo(video, liked, feedback)
                loadPendingVideos()
                loadRatedVideosInternal()
            } catch (t: Throwable) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = t.message ?: "Unable to submit rating"
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    private suspend fun loadPendingVideos() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        try {
            allVideos = repository.fetchPendingVideos()
            updateState()
        } catch (t: Throwable) {
            _state.value = _state.value.copy(
                isLoading = false,
                error = t.message ?: "Unable to load videos"
            )
        }
    }

    fun loadRatedVideos() {
        viewModelScope.launch {
            loadRatedVideosInternal()
        }
    }

    private suspend fun loadRatedVideosInternal() {
        _state.value = _state.value.copy(isLoadingRatedVideos = true, error = null)
        try {
            val rated = repository.fetchRatedVideos()
            _state.value = _state.value.copy(
                isLoadingRatedVideos = false,
                ratedVideos = rated
            )
        } catch (t: Throwable) {
            _state.value = _state.value.copy(
                isLoadingRatedVideos = false,
                error = t.message ?: "Unable to load rated videos"
            )
        }
    }

    private fun updateState() {
        _state.value = _state.value.copy(
            isLoading = false,
            error = null,
            videos = allVideos.filter { it.liked == null }
        )
    }
}

class VideoReviewViewModelFactory(
    private val repository: VideoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoReviewViewModel::class.java)) {
            return VideoReviewViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: modelClass")
    }
}

data class VideoFeedState(
    val isLoading: Boolean = false,
    val isLoadingRatedVideos: Boolean = false,
    val error: String? = null,
    val videos: List<VideoItem> = emptyList(),
    val ratedVideos: List<VideoItem> = emptyList()
)
