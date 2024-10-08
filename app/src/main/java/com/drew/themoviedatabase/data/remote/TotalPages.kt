package com.drew.themoviedatabase.data.remote

import com.google.gson.annotations.SerializedName

data class TotalPages(
    @SerializedName("total_pages")
    private val totalPages: Int? = null
) {
    fun getTotalPages(): Int? {
        return totalPages
    }
}
