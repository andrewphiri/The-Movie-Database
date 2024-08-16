package com.drew.themoviedatabase.data

import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import com.drew.themoviedatabase.Network.API_KEY
import com.drew.themoviedatabase.Network.CastApiService
import com.drew.themoviedatabase.Network.CombinedCreditsResponse
import com.drew.themoviedatabase.Network.PersonPhotosResponse
import com.drew.themoviedatabase.POJO.PersonDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import javax.inject.Inject

class PersonRepository @Inject constructor(
    private val castApiService: CastApiService
) {
    private val systemLocale = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
    private val locale = if (systemLocale != null) {
        Locale(systemLocale.language, systemLocale.country)
    } else {
        Locale("en", "US")
    }
    private val defaultLocale = locale.toLanguageTag()
    private val imageLanguage = systemLocale?.language ?: "en"

    fun getPersonDetails(personId : Int, callback: (Response<PersonDetails?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
               castApiService.getPersonDetails(personId, apiKey = API_KEY, language = defaultLocale).enqueue(object :
                   Callback<PersonDetails?> {
                   override fun onResponse(p0: Call<PersonDetails?>, p1: Response<PersonDetails?>) {
                       if (p1.isSuccessful) {
                           callback(p1)
                       }
                   }

                   override fun onFailure(p0: Call<PersonDetails?>, p1: Throwable) {
                       callback(Response.success(null))
                   }
               })
            } catch (e: Exception) {
                e.printStackTrace()
                callback(Response.success(null))
            }
        }
    }

    fun getPersonImages(personId: Int, callback: (Response<PersonPhotosResponse?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                castApiService.getPersonPhotos(personId, apiKey = API_KEY, language = defaultLocale).enqueue(object : Callback<PersonPhotosResponse?> {
                    override fun onResponse(
                        p0: Call<PersonPhotosResponse?>,
                        p1: Response<PersonPhotosResponse?>
                    ) {
                        if (p1.isSuccessful) {
                            callback(p1)
                        }
                    }
                    override fun onFailure(p0: Call<PersonPhotosResponse?>, p1: Throwable) {
                        callback(Response.success(null))
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                callback(Response.success(null))
            }
        }
    }

    fun getCombinedCredits(personId: Int, callback: (Response<CombinedCreditsResponse?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                castApiService.getCombinedCredits(personId, API_KEY, defaultLocale).enqueue(object : Callback<CombinedCreditsResponse?> {
                    override fun onResponse(
                        p0: Call<CombinedCreditsResponse?>,
                        p1: Response<CombinedCreditsResponse?>
                    ) {
                        if (p1.isSuccessful) {
                            callback(p1)
                        }
                    }

                    override fun onFailure(p0: Call<CombinedCreditsResponse?>, p1: Throwable) {
                        callback(Response.success(null))
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                callback(Response.success(null))
            }
        }
    }
}