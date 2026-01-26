package com.elmon.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VideoRating(
    @PrimaryKey val videoId: String,
    val liked: Boolean,
    val timestamp: Long
)
