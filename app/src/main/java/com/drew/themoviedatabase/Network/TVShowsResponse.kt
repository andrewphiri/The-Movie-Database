package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.TVShow
import com.google.gson.annotations.SerializedName

data class TVShowsResponse(
    val page: Int,
    private val results: List<TVShow>?,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
) {
    fun getTVShows(): List<TVShow>? {
        return results
    }
}
