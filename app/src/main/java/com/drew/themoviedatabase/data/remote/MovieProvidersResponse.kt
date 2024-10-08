package com.drew.themoviedatabase.data.remote

import com.google.gson.annotations.SerializedName

data class MovieProvidersResponse(
    val id: Int,
    @SerializedName("results")
    val results: Map<String, com.drew.themoviedatabase.data.model.MovieProviders>
)
