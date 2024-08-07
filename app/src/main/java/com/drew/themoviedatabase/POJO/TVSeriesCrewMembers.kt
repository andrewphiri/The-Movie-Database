package com.drew.themoviedatabase.POJO

import com.google.gson.annotations.SerializedName


// Define data classes to match the structure of your JSON
data class Crew(
    @SerializedName("department") val department: String,
    @SerializedName("job") val job: String,
    @SerializedName("credit_id") val creditId: String,
    @SerializedName("adult") val adult: Boolean,
    @SerializedName("gender") val gender: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("known_for_department") val knownForDepartment: String,
    @SerializedName("name") val name: String,
    @SerializedName("original_name") val originalName: String,
    @SerializedName("popularity") val popularity: Double,
    @SerializedName("profile_path") val profilePath: String?
)

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
    @SerializedName("crew") val crew: List<Crew>,
    @SerializedName("guest_stars") val guestStars: List<Any>
)

data class SeasonResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("air_date") val airDate: String,
    @SerializedName("episodes") val episodes: List<Episode>
)


