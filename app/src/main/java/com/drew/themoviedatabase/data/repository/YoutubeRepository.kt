package com.drew.themoviedatabase.data.repository

import com.drew.themoviedatabase.data.remote.PlaylistResponse
import com.drew.themoviedatabase.data.remote.YoutubeApiService
import com.drew.themoviedatabase.data.remote.YoutubeResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class YoutubeRepository @Inject constructor(
    private val youtubeApiService: YoutubeApiService
) {

    suspend fun getPlaylistID(id: String) : YoutubeResponse? {
        return try {
            coroutineScope {
               withContext(Dispatchers.IO) {
                   try {
                       val response = youtubeApiService.retrievePlaylistID(id = id,
                           apiKey = com.drew.themoviedatabase.data.remote.YOUTUBE_API_KEY)?.execute()

                       if (response?.isSuccessful == true) {
                           response.body()
                       } else {
                           null
                       }
                   } catch (e: Exception) {
                       e.printStackTrace()
                       null
                   }
               }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getPlaylistItems(playlistId: String) : PlaylistResponse? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response = youtubeApiService.retrievePlaylistItems(playlistId = playlistId ,
                            apiKey = com.drew.themoviedatabase.data.remote.YOUTUBE_API_KEY)?.execute()

                        if (response?.isSuccessful == true) {
                            response.body()
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

