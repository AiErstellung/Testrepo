package com.elmon.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.elmon.app.data.model.VideoRating

@Dao
interface VideoRatingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rating: VideoRating)

    @Query("SELECT videoId FROM VideoRating")
    suspend fun getRatedIds(): List<String>

    @Query("SELECT * FROM VideoRating")
    suspend fun getAll(): List<VideoRating>
}
