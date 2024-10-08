package com.drew.themoviedatabase.data.remote

import com.google.gson.annotations.SerializedName

data class CombinedCreditsResponse(
    @SerializedName("cast")
    private val combinedCreditsResponse: List<com.drew.themoviedatabase.data.model.CombinedCredits>
) {
    fun getCombinedCredits(): List<com.drew.themoviedatabase.data.model.CombinedCredits> {
        return combinedCreditsResponse
    }
}