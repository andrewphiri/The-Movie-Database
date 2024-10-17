package com.drew.themoviedatabase.data.remote

import com.drew.themoviedatabase.data.model.PlaylistItem
import com.google.gson.annotations.SerializedName

data class YoutubeResponse(
    @SerializedName("items") val items: List<PlaylistItem>
)
