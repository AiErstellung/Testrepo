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
    private val ratedIds = mutableSetOf<String>()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val fetched = repository.fetchPendingVideos()
                allVideos = fetched
                ratedIds.clear()
                ratedIds.addAll(repository.getRatedIds())
                updateState()
            } catch (t: Throwable) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = t.message ?: "Unable to load videos"
                )
            }
        }
    }

    fun rateVideo(video: VideoItem, liked: Boolean) {
        viewModelScope.launch {
            repository.rateVideo(video.id, liked)
            ratedIds.add(video.id)
            updateState()
        }
    }

    private fun updateState() {
        val filtered = allVideos.filter { it.id !in ratedIds }
        _state.value = VideoFeedState(
            isLoading = false,
            error = null,
            videos = filtered
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
    val error: String? = null,
    val videos: List<VideoItem> = emptyList()
)
