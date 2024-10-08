package com.drew.themoviedatabase.data.model

import com.google.gson.annotations.SerializedName


data class MovieProviders(
    val link: String,
    val flatrate: List<com.drew.themoviedatabase.data.model.Provider>,
    val buy: List<com.drew.themoviedatabase.data.model.Provider>,
    val rent: List<com.drew.themoviedatabase.data.model.Provider>
)

data class Provider(
    @SerializedName("logo_path") val logoPath: String,
    @SerializedName("provider_id") val providerId: Int,
    @SerializedName("provider_name") val providerName: String,
    @SerializedName("display_priority") val displayPriority: Int
)
