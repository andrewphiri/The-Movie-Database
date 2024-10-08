package com.drew.themoviedatabase.data.model

import com.google.gson.annotations.SerializedName

class ReleaseDate(
        @SerializedName("certification")
        val certification: String,
//        @SerialName("descriptors")
//        val descriptors: List<String>,
//        @SerializedName("iso_639_1")
//        val iso6391: String,
//        @SerializedName("note")
//        val note: String,
//        @SerializedName("release_date")
//        val releaseDate: String,
//        @SerializedName("type")
//        val type: Int
    )

data class CountryReleaseDates(
    @SerializedName("iso_3166_1")
    val iso31661: String,
    @SerializedName("release_dates")
    val releaseDates: List<com.drew.themoviedatabase.data.model.ReleaseDate>
)

