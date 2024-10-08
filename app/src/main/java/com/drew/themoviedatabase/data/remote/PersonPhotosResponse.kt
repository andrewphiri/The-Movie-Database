package com.drew.themoviedatabase.data.remote

import com.google.gson.annotations.SerializedName

data class PersonPhotosResponse(
    @SerializedName("profiles")
    private val personPhotosResponse: List<com.drew.themoviedatabase.data.model.Photos>?
) {
    fun getPersonPhotos(): List<com.drew.themoviedatabase.data.model.Photos>? {
        return personPhotosResponse
    }
}
