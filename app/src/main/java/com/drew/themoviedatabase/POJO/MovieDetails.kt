package com.drew.themoviedatabase.POJO

import com.google.gson.annotations.SerializedName

data class MovieDetails(
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("belongs_to_collection")
    val belongsToCollection: Any?,
    val budget: Int,
    val genres: List<Genre>,
    val homepage: String?,
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
    val voteCount: Int
)

data class Genre(
    val id: Int,
    val name: String
)

data class ProductionCompany(
    val id: Int,
    @SerializedName("logo_path") val logoPath: String?,
    val name: String,
    @SerializedName("origin_country") val originCountry: String
)

data class ProductionCountry(
    @SerializedName("iso_3166_1") val iso31661: String,
    val name: String
)

data class SpokenLanguage(
    @SerializedName("english_name") val englishName: String,
    @SerializedName("iso_639_1") val iso6391: String,
    val name: String
)



// Getters and setters
// Inner classes for Genre, ProductionCompany, ProductionCountry, SpokenLanguage
