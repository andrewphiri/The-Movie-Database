package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.MovieProviders
import com.google.gson.annotations.SerializedName

data class MovieProvidersResponse(
    val id: Int,
    @SerializedName("results")
    val results: Map<String, MovieProviders>
)
