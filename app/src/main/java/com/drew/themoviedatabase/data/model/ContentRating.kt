package com.drew.themoviedatabase.data.model

import com.google.gson.annotations.SerializedName

data class ContentRating(
    @SerializedName("descriptors")
    val descriptors: List<String>,
    @SerializedName("iso_3166_1")
    val iso31661: String,
    @SerializedName("rating")
    val rating: String
)
