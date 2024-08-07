package com.drew.themoviedatabase.POJO

import com.google.gson.annotations.SerializedName

data class MovieProviders(
    val id: Int,
    val results: Map<String, RegionProviders>
)

data class RegionProviders(
    val link: String,
    val buy: List<Provider>,
    val rent: List<Provider>
)

data class Provider(
    @SerializedName("logo_path") val logoPath: String,
    @SerializedName("provider_id") val providerId: Int,
    @SerializedName("provider_name") val providerName: String,
    @SerializedName("display_priority") val displayPriority: Int
)
