package com.drew.themoviedatabase.data.remote

import com.google.gson.annotations.SerializedName

data class TVShowsResponse(
    val page: Int,
    private val results: List<com.drew.themoviedatabase.data.model.TVShow>?,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
) {
    fun getTVShows(): List<com.drew.themoviedatabase.data.model.TVShow>? {
        return results
    }
}
