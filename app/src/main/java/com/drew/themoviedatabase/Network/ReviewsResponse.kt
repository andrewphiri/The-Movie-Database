package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.Movie
import com.drew.themoviedatabase.POJO.Reviews
import com.google.gson.annotations.SerializedName

data class ReviewsResponse(
        @SerializedName("results")
        private val results: List<Reviews>? = null
) {
    fun getResults(): List<Reviews>? {
        return results
    }
}