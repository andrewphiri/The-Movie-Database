package com.drew.themoviedatabase.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.drew.themoviedatabase.data.model.YoutubeChannels

@Database(entities = [YoutubeChannels::class], version = 2, exportSchema = false)
abstract class ChannelDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
}