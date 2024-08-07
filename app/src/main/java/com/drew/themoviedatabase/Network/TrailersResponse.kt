package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.Trailers
import com.google.gson.annotations.SerializedName

data class TrailersResponse(
    @SerializedName("results")
    private val results: List<Trailers>? = null) {

    fun getResults(): List<Trailers>? {
        return results
    }

}

