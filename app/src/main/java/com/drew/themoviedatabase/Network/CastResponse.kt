package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.CastMembers
import com.drew.themoviedatabase.POJO.Crew
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class CastResponse(
    @SerializedName("cast")
    private val cast: List<CastMembers>? = null,
    @SerializedName("crew")
    private val crew: List<Crew>? = null
) {
    fun getCast(): List<CastMembers>? {
        return cast
    }
    fun getCrew(): List<Crew>? {
        return crew
    }
}