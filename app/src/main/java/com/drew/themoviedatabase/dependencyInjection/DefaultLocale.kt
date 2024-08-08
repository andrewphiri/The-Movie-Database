package com.drew.themoviedatabase.dependencyInjection

import android.content.res.Resources
import android.util.Log
import androidx.core.os.ConfigurationCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Locale

@Module
@InstallIn(SingletonComponent::class)
object DefaultLocale {

    @Provides
    fun provideDefaultLocale(): String {
        val systemLocale = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
        val defaultLocale = if (systemLocale != null) {
            Locale(systemLocale.language, systemLocale.country)
        } else {
            Locale("en", "US")
        }
        val languageTag = defaultLocale.toLanguageTag()
        return languageTag
    }

}