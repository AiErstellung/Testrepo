package com.elmon.app.repository

import com.elmon.app.data.db.VideoRatingDao
import com.elmon.app.data.model.VideoItem
import com.elmon.app.data.model.VideoRating
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class VideoRepository(
    private val storage: S3Storage,
    private val dao: VideoRatingDao
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchPendingVideos(): List<VideoItem> = withContext(Dispatchers.IO) {
        val body = storage.readObject("pending/videos.json")
        val payload = String(body, Charsets.UTF_8)
        json.decodeFromString(ListSerializer(VideoItem.serializer()), payload)
    }

    suspend fun getRatedIds(): Set<String> = withContext(Dispatchers.IO) {
        dao.getRatedIds().toSet()
    }

    suspend fun rateVideo(video: VideoItem, liked: Boolean, feedback: String?) = withContext(Dispatchers.IO) {
        val timestamp = System.currentTimeMillis()
        val storedFeedback = feedback?.takeIf { it.isNotBlank() }
        dao.insert(VideoRating(video.id, liked, timestamp, storedFeedback))

        val sourceKey = resolveKey(video)
        val targetFolder = if (liked) "good" else "bad"
        val targetKey = destinationKey(sourceKey, targetFolder)

        storage.copyObject(sourceKey, targetKey)
        storage.deleteObject(sourceKey)

        if (!liked) {
            storage.uploadFeedback(video.id, feedback ?: "", timestamp)
        }
    }

    suspend fun getAllRatings(): List<VideoRating> = withContext(Dispatchers.IO) {
        dao.getAll()
    }

    private fun resolveKey(video: VideoItem): String {
        return video.s3Key?.trimStart('/') ?: storage.keyFromUrl(video.url)
    }

    private fun destinationKey(sourceKey: String, targetFolder: String): String {
        val relative = sourceKey.removePrefix("pending/").trimStart('/')
        val suffix = if (relative.isBlank() || relative == sourceKey) {
            sourceKey.substringAfterLast('/')
        } else {
            relative
        }
        return "$targetFolder/$suffix".trimStart('/')
    }
}
