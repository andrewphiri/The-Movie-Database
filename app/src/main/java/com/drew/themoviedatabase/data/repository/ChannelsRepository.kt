package com.drew.themoviedatabase.data.repository

import com.drew.themoviedatabase.data.model.YoutubeChannels
import com.drew.themoviedatabase.data.room.ChannelDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChannelsRepository @Inject constructor(
    private val channelDao: ChannelDao
) {
    suspend fun insertChannel(channel: YoutubeChannels) =  channelDao.insertChannel(channel)

    suspend fun updateChannel(channel: YoutubeChannels) = channelDao.updateChannel(channel)

    fun getChannels() : Flow<List<YoutubeChannels>> = channelDao.getAllChannels()

    suspend fun deleteChannel(channelId: String) = channelDao.deleteChannel(channelId)

    fun getChannelByID(channelId: String) : Flow<YoutubeChannels> = channelDao.getChannelById(channelId)
}