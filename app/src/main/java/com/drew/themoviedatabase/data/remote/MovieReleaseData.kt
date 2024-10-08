package com.drew.themoviedatabase.data.remote

import com.google.gson.annotations.SerializedName

data class MovieReleaseData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("results")
    val results: List<com.drew.themoviedatabase.data.model.CountryReleaseDates>
)