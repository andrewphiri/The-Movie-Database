package com.drew.themoviedatabase.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.drew.themoviedatabase.data.model.YoutubeChannels
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    @Query("SELECT * FROM youtube_channels")
    fun getAllChannels(): Flow<List<YoutubeChannels>>

    @Query("SELECT * FROM youtube_channels WHERE id = :id")
    fun getChannelById(id: String): Flow<YoutubeChannels>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: YoutubeChannels)

    @Query("DELETE FROM youtube_channels WHERE id = :id")
    suspend fun deleteChannel(id: String)

    @Update
    suspend fun updateChannel(channel: YoutubeChannels)
}