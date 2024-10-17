package com.drew.themoviedatabase.data.remote

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeApiService {

    @GET("channels")
    fun retrievePlaylistID(
        @Query("part") part: String = "contentDetails",
        @Query("id") id: String,
        @Query("key") apiKey: String) : Call<YoutubeResponse?>?

    @GET("playlistItems")
    fun retrievePlaylistItems(
        @Query("part") part: String = "snippet,contentDetails,status",
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int = 50,
        @Query("key") apiKey: String) : Call<PlaylistResponse?>?

}