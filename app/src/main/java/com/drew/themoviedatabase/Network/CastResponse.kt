package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.CastMembers
import com.google.gson.annotations.SerializedName

class CastResponse(
    @SerializedName("cast")
    private val cast: List<CastMembers>? = null
) {
    fun getCast(): List<CastMembers>? {
        return cast
    }
}