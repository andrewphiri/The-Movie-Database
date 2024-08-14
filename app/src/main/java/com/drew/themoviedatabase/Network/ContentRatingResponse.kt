package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.ContentRating
import com.google.gson.annotations.SerializedName

data class ContentRatingResponse(
    @SerializedName("results")
    val results: List<ContentRating>
)
