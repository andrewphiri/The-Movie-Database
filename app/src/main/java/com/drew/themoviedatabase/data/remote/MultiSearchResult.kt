package com.drew.themoviedatabase.data.remote

sealed class MultiSearchResult {
    data class Movie (
        val id: Int,
        val title: String,
        val overview: String,
        val poster_path: String?,
        val release_date: String,
        val vote_average: Double,
        val media_type: String
    ) : MultiSearchResult()

    data class TV(
        val id: Int,
        val name: String,
        val overview: String,
        val poster_path: String?,
        val first_air_date: String,
        val vote_average: Double,
        val media_type: String
    ) : MultiSearchResult()

    data class Person(
        val id: Int,
        val name: String,
        val known_for_department: String,
        val profile_path: String?,
        val media_type: String
    ) : MultiSearchResult()
}