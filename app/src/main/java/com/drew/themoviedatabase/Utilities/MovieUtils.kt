package com.drew.themoviedatabase.Utilities

import com.drew.themoviedatabase.POJO.Trailers

fun findPreferredVideo(trailers: List<Trailers?>?): String? {
    val officialTrailer = trailers?.find { it?.type == "Trailer" && it.name == "Official Trailer" }?.key
    if (officialTrailer != null) return officialTrailer

    val officialTeaser = trailers?.find { it?.type == "Teaser" && it.name == "Official Teaser" }?.key
    if (officialTeaser != null) return officialTeaser

    return trailers?.find { it?.type == "Teaser" }?.key
}