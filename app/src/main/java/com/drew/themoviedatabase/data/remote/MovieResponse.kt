package com.drew.themoviedatabase.data.remote

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("results")
    private val results: List<com.drew.themoviedatabase.data.model.Movie>? = null,
    @SerializedName("total_pages")
    private val totalPages: Int? = null
) {


    fun getResults(): List<com.drew.themoviedatabase.data.model.Movie>? {
        return results
    }
}