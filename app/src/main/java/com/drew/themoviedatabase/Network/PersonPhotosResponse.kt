package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.Photos
import com.google.gson.annotations.SerializedName

data class PersonPhotosResponse(
    @SerializedName("profiles")
    private val personPhotosResponse: List<Photos>?
) {
    fun getPersonPhotos(): List<Photos>? {
        return personPhotosResponse
    }
}
