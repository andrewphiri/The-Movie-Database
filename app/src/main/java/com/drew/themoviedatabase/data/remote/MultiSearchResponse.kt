package com.drew.themoviedatabase.data.remote

data class MultiSearchResponse(
    val page: Int,
    val results: List<MultiSearchResult>,
    val total_pages: Int,
    val total_results: Int
)
