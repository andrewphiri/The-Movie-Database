package com.drew.themoviedatabase.data.remote

import com.google.gson.annotations.SerializedName

data class ReviewsResponse(
        @SerializedName("results")
        private val results: List<com.drew.themoviedatabase.data.model.Reviews>? = null
) {
    fun getResults(): List<com.drew.themoviedatabase.data.model.Reviews>? {
        return results
    }
}