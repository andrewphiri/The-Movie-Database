package com.drew.themoviedatabase.data.model


import com.drew.themoviedatabase.data.remote.CombinedCreditsResponse
import com.drew.themoviedatabase.data.remote.PersonPhotosResponse
import com.google.gson.annotations.SerializedName

data class PersonDetails(
    val adult: Boolean,
    @SerializedName("also_known_as")
    val alsoKnownAs: List<String>,
    @SerializedName("biography")
    val biography: String,
    @SerializedName("birthday")
    val birthday: String,
    @SerializedName("deathday")
    val deathDay: String?,
    @SerializedName("gender")
    val gender: Int,
    @SerializedName("homepage")
    val homepage: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("imdb_id")
    val imdbId: String,
    @SerializedName("known_for_department")
    val knownForDepartment: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("place_of_birth")
    val placeOfBirth: String,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("profile_path")
    val profilePath: String,
    @SerializedName("combined_credits")
    val combinedCredits: CombinedCreditsResponse,
    @SerializedName("images")
    val images: PersonPhotosResponse
)
