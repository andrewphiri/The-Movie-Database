package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.Provider
import com.google.gson.annotations.SerializedName

data class TVProvidersResponse(
    val id: Int,
    val results: Map<String, TVProviders>
)


data class TVProviders(
    val link: String,
    val flatrate: List<Provider>?,
    val ads: List<Provider>?,
    val buy: List<Provider>?,
    val rent: List<Provider>?,
    val free: List<Provider>?
)


