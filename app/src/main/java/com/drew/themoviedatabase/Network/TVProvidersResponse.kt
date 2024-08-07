package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.Provider
import com.google.gson.annotations.SerializedName

data class TVProvidersResponse(
    val id: Int,
    val results: Map<String, CountryResults>
)


data class CountryResults(
    val link: String,
    val flatrate: List<Provider>?,
    val ads: List<Provider>?,
    val buy: List<Provider>?,
    val free: List<Provider>?
)

data class Response(
    val id: Int,
    val results: Map<String, CountryResults>
)
