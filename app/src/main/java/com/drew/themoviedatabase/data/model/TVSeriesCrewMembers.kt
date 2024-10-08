package com.drew.themoviedatabase.data.model

import com.google.gson.annotations.SerializedName


// Define data classes to match the structure of your JSON
data class Episode(
    @SerializedName("air_date") val airDate: String?,
    @SerializedName("episode_number") val episodeNumber: Int,
    @SerializedName("episode_type") val episodeType: String,
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("production_code") val productionCode: String,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("season_number") val seasonNumber: Int,
    @SerializedName("show_id") val showId: Int,
    @SerializedName("still_path") val stillPath: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("crew") val crew: List<com.drew.themoviedatabase.data.model.Crew>,
    @SerializedName("guest_stars") val guestStars: List<Any>
)

data class SeasonResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("air_date") val airDate: String,
    @SerializedName("episodes") val episodes: List<com.drew.themoviedatabase.data.model.Episode>
)


