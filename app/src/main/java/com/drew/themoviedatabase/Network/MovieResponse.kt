package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.Movie
import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("results")
    private val results: List<Movie>? = null
) {


    fun getResults(): List<Movie>? {
        return results
    }
}