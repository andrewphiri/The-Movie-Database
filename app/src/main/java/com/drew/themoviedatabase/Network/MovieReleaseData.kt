package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.CountryReleaseDates
import com.google.gson.annotations.SerializedName

data class MovieReleaseData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("results")
    val results: List<CountryReleaseDates>
)