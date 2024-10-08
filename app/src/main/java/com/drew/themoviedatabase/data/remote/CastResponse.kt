package com.drew.themoviedatabase.data.remote

import com.google.gson.annotations.SerializedName

data class CastResponse(
    @SerializedName("cast")
    private val cast: List<com.drew.themoviedatabase.data.model.CastMembers>? = null,
    @SerializedName("crew")
    private val crew: List<com.drew.themoviedatabase.data.model.Crew>? = null
) {
    fun getCast(): List<com.drew.themoviedatabase.data.model.CastMembers>? {
        return cast
    }
    fun getCrew(): List<com.drew.themoviedatabase.data.model.Crew>? {
        return crew
    }
}