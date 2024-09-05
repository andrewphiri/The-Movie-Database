package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.Genre
import com.drew.themoviedatabase.POJO.SpokenLanguage
import com.google.gson.annotations.SerializedName

data class MovieDetailsResponse(
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("belongs_to_collection")
    val belongsToCollection: Any?,
    val budget: Int,
    val genres: List<Genre>,
    val homepage: String?,
    val spoken_languages: List<SpokenLanguage>,
    val id: Int,
    @SerializedName("imdb_id")
    val imdbId: String?,
    @SerializedName("origin_country")
    val originCountry: List<String>,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("original_title")
    val originalTitle: String,
    val overview: String,
    val popularity: Double,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    val revenue: Int,
    val runtime: Int,
    val status: String,
    val tagline: String?,
    val title: String,
    val video: Boolean,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("credits")
    val credits: CastResponse,
    @SerializedName("videos")
    val videos: TrailersResponse,
    @SerializedName("release_dates")
    val certifications: MovieReleaseData,
    @SerializedName("watch/providers")
    val watchProviders: MovieProvidersResponse
)
