package com.drew.themoviedatabase.POJO

import com.drew.themoviedatabase.Network.ContentRatingResponse
import com.drew.themoviedatabase.Network.MovieProvidersResponse
import com.drew.themoviedatabase.Network.TVProvidersResponse
import com.google.gson.annotations.SerializedName

data class TVShowDetails(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("first_air_date")
    val firstAirDate: String,
    @SerializedName("last_air_date")
    val lastAirDate: String,
    @SerializedName("number_of_seasons")
    val numberOfSeasons: Int,
    @SerializedName("number_of_episodes")
    val numberOfEpisodes: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("origin_country")
    val originCountry: List<String>,
    @SerializedName("genres")
    val genres: List<TVGenre>,
    @SerializedName("created_by")
    val createdBy: List<Creator>,
    @SerializedName("networks")
    val networks: List<Network>,
    @SerializedName("production_companies")
    val productionCompanies: List<TVProductionCompany>,
    @SerializedName("seasons")
    val seasons: List<Season>,
    @SerializedName("poster_path")
    val posterPath: String? = null,
    @SerializedName("backdrop_path")
    val backdropPath: String? = null,
    @SerializedName("homepage")
    val homepage: String? = null,
    @SerializedName("tagline")
    val tagline: String? = null,
    @SerializedName("content_ratings")
    val contentRatings: ContentRatingResponse?,
    @SerializedName("watch/providers")
    val watchProviders: TVProvidersResponse
)

data class TVGenre(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)


data class Creator(
    @SerializedName("id")
    val id: Int,
    @SerializedName("credit_id")
    val creditId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("original_name")
    val originalName: String,
    @SerializedName("gender")
    val gender: Int,
    @SerializedName("profile_path")
    val profilePath: String?
)

data class Network(
    @SerializedName("id")
    val id: Int,
    @SerializedName("logo_path")
    val logoPath: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("origin_country")
    val originCountry: String
)

data class TVProductionCompany(
    @SerializedName("id")
    val id: Int,
    @SerializedName("logo_path")
    val logoPath: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("origin_country")
    val originCountry: String
)

data class Season(
    @SerializedName("air_date")
    val airDate: String?,
    @SerializedName("episode_count")
    val episodeCount: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("overview")
    val overview: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("season_number")
    val seasonNumber: Int,
    @SerializedName("vote_average")
    val voteAverage: Double = 0.0
)

fun TVShowDetails.displayDetails() {
    println("""
        Name: $name
        Overview: $overview
        First Air Date: $firstAirDate
        Last Air Date: $lastAirDate
        Number of Seasons: $numberOfSeasons
        Number of Episodes: $numberOfEpisodes
        Status: $status
        Popularity: $popularity
        Vote Average: $voteAverage
        Vote Count: $voteCount
        Original Language: $originalLanguage
        Origin Country: ${originCountry.joinToString(", ")}
        Genres: ${genres.joinToString(", ") { it.name }}
        Created By: ${createdBy.joinToString(", ") { it.name }}
        Networks: ${networks.joinToString(", ") { it.name }}
        Production Companies: ${productionCompanies.joinToString(", ") { it.name }}
        Seasons: ${seasons.joinToString(", ") { "Season ${it.seasonNumber}" }}
        Poster Path: $posterPath
        Backdrop Path: $backdropPath
        Homepage: $homepage
        Tagline: $tagline
    """.trimIndent())
}

