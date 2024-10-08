package com.drew.themoviedatabase.data.remote

data class TVProvidersResponse(
    val id: Int,
    val results: Map<String, TVProviders>
)


data class TVProviders(
    val link: String,
    val flatrate: List<com.drew.themoviedatabase.data.model.Provider>?,
    val ads: List<com.drew.themoviedatabase.data.model.Provider>?,
    val buy: List<com.drew.themoviedatabase.data.model.Provider>?,
    val rent: List<com.drew.themoviedatabase.data.model.Provider>?,
    val free: List<com.drew.themoviedatabase.data.model.Provider>?
)


