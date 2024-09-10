package com.drew.themoviedatabase.Network

data class MultiSearchResponse(
    val page: Int,
    val results: List<MultiSearchResult>,
    val total_pages: Int,
    val total_results: Int
)
