package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.CombinedCredits
import com.google.gson.annotations.SerializedName

data class CombinedCreditsResponse(
    @SerializedName("cast")
    private val combinedCreditsResponse: List<CombinedCredits>
) {
    fun getCombinedCredits(): List<CombinedCredits> {
        return combinedCreditsResponse
    }
}