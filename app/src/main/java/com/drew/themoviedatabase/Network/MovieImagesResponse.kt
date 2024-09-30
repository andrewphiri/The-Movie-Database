package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.Photos
import com.google.gson.annotations.SerializedName

data class MovieImagesResponse(
    @SerializedName("backdrops")
    val backdrops: List<Photos>?,
    @SerializedName("logos")
    val logos: List<Photos>?,
    @SerializedName("posters")
    val posters: List<Photos>?
) {
    fun getAllImages() : List<Photos> {
        val allImages = mutableListOf<Photos>()
        backdrops?.let { allImages.addAll(it) }
        logos?.let { allImages.addAll(it) }
        posters?.let { allImages.addAll(it) }
        return allImages
    }
}
