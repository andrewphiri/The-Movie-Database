package com.drew.themoviedatabase.data.model

import com.google.gson.annotations.SerializedName

data class VideoItems(
    @SerializedName("contentDetails")
    val videoItem: VideoDetails
)
