package com.drew.themoviedatabase.Utilities

import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import com.drew.themoviedatabase.POJO.Trailers
import java.util.Locale

fun findPreferredVideo(trailers: List<Trailers?>?): String? {
    val officialTrailer = trailers?.find { it?.type == "Trailer" && it.name == "Official Trailer" }?.key
    if (officialTrailer != null) return officialTrailer

    val officialTeaser = trailers?.find { it?.type == "Teaser" && it.name == "Official Teaser" }?.key
    if (officialTeaser != null) return officialTeaser

    val trailer = trailers?.find { it?.type == "Teaser" }?.key
    if (trailer != null) return trailer

    val featurette = trailers?.find { it?.type == "Featurette" }?.key
    if (featurette != null) return featurette

    return trailers?.get(0)?.key
}

fun getWatchRegion(): String {
    val systemLocale = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
    val locale = if (systemLocale != null) {
        Locale(systemLocale.language, systemLocale.country)
    } else {
        Locale("en", "US")
    }
    return locale.country
}