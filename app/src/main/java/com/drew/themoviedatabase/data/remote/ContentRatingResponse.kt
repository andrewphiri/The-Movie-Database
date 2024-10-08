package com.drew.themoviedatabase.data.remote

import com.google.gson.annotations.SerializedName

data class ContentRatingResponse(
    @SerializedName("results")
    val results: List<com.drew.themoviedatabase.data.model.ContentRating>
)
