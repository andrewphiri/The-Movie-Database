package com.drew.themoviedatabase.data.remote

import com.drew.themoviedatabase.data.model.Trailers
import com.google.gson.annotations.SerializedName

data class TrailersResponse(
    @SerializedName("results")
    private val results: List<Trailers>? = null) {

    fun getResults(): List<Trailers>? {
        return results
    }

}

