package com.drew.themoviedatabase.data.remote

import com.drew.themoviedatabase.data.model.Episode
import com.google.gson.annotations.SerializedName

data class SeasonResponse(
    val _id: String,
    @SerializedName("air_date") val airDate: String,
    val name: String,
    val overview: String?,
    val poster_path: String?,
    val season_number: Int,
    val id: Int,
    val vote_average: Double,
    @SerializedName("episodes") val episodes: List<Episode>
)
