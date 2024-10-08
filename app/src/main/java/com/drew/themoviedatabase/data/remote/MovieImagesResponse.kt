package com.drew.themoviedatabase.data.remote

import com.google.gson.annotations.SerializedName

data class MovieImagesResponse(
    @SerializedName("backdrops")
    val backdrops: List<com.drew.themoviedatabase.data.model.Photos>?,
    @SerializedName("logos")
    val logos: List<com.drew.themoviedatabase.data.model.Photos>?,
    @SerializedName("posters")
    val posters: List<com.drew.themoviedatabase.data.model.Photos>?
) {
    fun getAllImages() : List<com.drew.themoviedatabase.data.model.Photos> {
        val allImages = mutableListOf<com.drew.themoviedatabase.data.model.Photos>()
        backdrops?.let { allImages.addAll(it) }
        logos?.let { allImages.addAll(it) }
        posters?.let { allImages.addAll(it) }
        return allImages
    }
}
