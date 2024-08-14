package com.drew.themoviedatabase.Utilities

import android.content.res.Resources
import android.icu.util.ULocale
import androidx.core.os.ConfigurationCompat
import java.text.NumberFormat
import java.util.Locale

fun currencyFormatter(currency: Double?): String {
//    val systemLocale = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
//    val defaultLocale = if (systemLocale != null) {
//        Locale(systemLocale.language, systemLocale.country)
//    } else {
//        Locale("en", "US")
//    }
//    val languageTag = defaultLocale.toLanguageTag()
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "US"))
    return formatter.format(currency)
}