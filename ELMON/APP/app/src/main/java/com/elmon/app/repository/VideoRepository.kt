package com.elmon.app.repository

import com.elmon.app.data.db.VideoRatingDao
import com.elmon.app.data.model.VideoItem
import com.elmon.app.data.model.VideoRating
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class VideoRepository(
    private val videosJsonUrl: String,
    private val httpClient: OkHttpClient,
    private val dao: VideoRatingDao
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchPendingVideos(): List<VideoItem> = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(videosJsonUrl).get().build()
        val response = httpClient.newCall(request).execute()
        response.use {
            if (!it.isSuccessful) {
                throw IOException("Failed to fetch videos.json: ${'$'}{it.code}")
            }
            val body = it.body?.string() ?: throw IOException("videos.json response was empty")
            json.decodeFromString(ListSerializer(VideoItem.serializer()), body)
        }
    }

    suspend fun getRatedIds(): Set<String> = withContext(Dispatchers.IO) {
        dao.getRatedIds().toSet()
    }

    suspend fun rateVideo(videoId: String, liked: Boolean) = withContext(Dispatchers.IO) {
        dao.insert(VideoRating(videoId, liked, System.currentTimeMillis()))
    }

    suspend fun getAllRatings(): List<VideoRating> = withContext(Dispatchers.IO) {
        dao.getAll()
    }
}
