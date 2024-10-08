package com.drew.themoviedatabase.data.remote

import com.google.gson.annotations.SerializedName

data class TVShowDetailsWithCastAndVideos(
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
    val genres: List<com.drew.themoviedatabase.data.model.Genre>,
    @SerializedName("created_by")
    val createdBy: List<com.drew.themoviedatabase.data.model.Creator>,
    @SerializedName("networks")
    val networks: List<com.drew.themoviedatabase.data.model.Network>,
    @SerializedName("production_companies")
    val productionCompanies: List<com.drew.themoviedatabase.data.model.TVProductionCompany>,
    @SerializedName("seasons")
    val seasons: List<com.drew.themoviedatabase.data.model.Season>,
    @SerializedName("poster_path")
    val posterPath: String? = null,
    @SerializedName("backdrop_path")
    val backdropPath: String? = null,
    @SerializedName("homepage")
    val homepage: String? = null,
    @SerializedName("tagline")
    val tagline: String? = null,
    @SerializedName("content_ratings")
    val contentRatings: com.drew.themoviedatabase.data.remote.ContentRatingResponse,
    @SerializedName("credits")
    val credits: com.drew.themoviedatabase.data.remote.CastResponse,
    @SerializedName("videos")
    val videos: TrailersResponse,
    @SerializedName("watch/providers")
    val watchProviders: TVProvidersResponse
)
