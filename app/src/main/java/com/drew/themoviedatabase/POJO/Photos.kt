package com.drew.themoviedatabase.POJO

import com.google.gson.annotations.SerializedName

data class Photos(
    @SerializedName("aspect_ratio")
    val aspectRatio: Double,
    @SerializedName("height")
    val height: Double,
    @SerializedName("iso_639_1")
    val iso_639_1: String,
    @SerializedName("file_path")
    val filePath : String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Double,
    @SerializedName("width")
    val width: Double
)
