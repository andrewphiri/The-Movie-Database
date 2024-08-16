package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.Photos
import com.google.gson.annotations.SerializedName

data class MovieImagesResponse(
    @SerializedName("backdrops")
    val backdrops: List<Photos>?,
    @SerializedName("logos")
    val logos: List<Photos>?,
    @SerializedName("posters")
    val posters: List<Photos>?
)
