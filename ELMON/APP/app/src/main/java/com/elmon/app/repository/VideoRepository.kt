package com.elmon.app.repository

import com.elmon.app.data.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class VideoRepository(
    private val storage: S3Storage
) {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = false }
    private val listKey = "pending/videos.json"
    private val listSerializer = ListSerializer(VideoItem.serializer())

    suspend fun fetchPendingVideos(): List<VideoItem> = withContext(Dispatchers.IO) {
        addPlaybackUrls(
            loadVideoList()
                .filter { it.liked == null }
        )
    }

    suspend fun rateVideo(video: VideoItem, liked: Boolean, feedback: String?) = withContext(Dispatchers.IO) {
        val allVideos = loadVideoList()
        val trimmedFeedback = feedback?.takeIf { it.isNotBlank() }
        if (allVideos.none { it.id == video.id }) {
            throw IllegalStateException("Video ${video.id} is not present in $listKey")
        }
        val currentTimestamp = System.currentTimeMillis()

        val updatedList = allVideos.map { item ->
            if (item.id == video.id) {
                item.copy(
                    liked = liked,
                    feedback = if (liked) null else trimmedFeedback,
                    ratedAt = currentTimestamp
                )
            } else {
                item
            }
        }

        storage.writeObject(
            listKey,
            json.encodeToString(listSerializer, updatedList).toByteArray(Charsets.UTF_8),
            "application/json; charset=utf-8"
        )
    }

    suspend fun fetchRatedVideos(): List<VideoItem> = withContext(Dispatchers.IO) {
        addPlaybackUrls(
            loadVideoList()
                .filter { it.liked != null }
                .sortedByDescending { it.ratedAt ?: 0L }
        )
    }

    private fun loadVideoList(): List<VideoItem> {
        val body = storage.readObject(listKey)
        val payload = String(body, Charsets.UTF_8)
        return json.decodeFromString(listSerializer, payload)
    }

    private fun addPlaybackUrls(videos: List<VideoItem>): List<VideoItem> {
        if (videos.isEmpty()) return videos
        return videos.map { video ->
            val key = video.s3Key?.trim()
            if (key.isNullOrBlank()) {
                video
            } else {
                video.copy(url = storage.presignGetObject(key))
            }
        }
    }
}
