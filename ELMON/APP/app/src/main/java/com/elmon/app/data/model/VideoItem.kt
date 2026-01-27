package com.elmon.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VideoItem(
    val id: String,
    val title: String,
    val url: String,
    val s3Key: String? = null
)
