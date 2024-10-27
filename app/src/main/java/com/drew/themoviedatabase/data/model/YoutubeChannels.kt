package com.drew.themoviedatabase.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "youtube_channels")
data class YoutubeChannels(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val channelName: String,
    val channelID: String,
    val isChannelEnabled: Boolean
)
