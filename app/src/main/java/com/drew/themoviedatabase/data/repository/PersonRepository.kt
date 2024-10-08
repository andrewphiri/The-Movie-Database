package com.drew.themoviedatabase.data.repository

import com.drew.themoviedatabase.Utilities.defaultLocale
import com.drew.themoviedatabase.data.remote.CastApiService
import com.drew.themoviedatabase.data.remote.CombinedCreditsResponse
import com.drew.themoviedatabase.data.remote.PersonPhotosResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class PersonRepository @Inject constructor(
    private val castApiService: CastApiService
) {
    fun getPersonDetails(personId : Int, callback: (Response<com.drew.themoviedatabase.data.model.PersonDetails?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
               castApiService.getPersonDetails(personId, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale()).enqueue(object :
                   Callback<com.drew.themoviedatabase.data.model.PersonDetails?> {
                   override fun onResponse(p0: Call<com.drew.themoviedatabase.data.model.PersonDetails?>, p1: Response<com.drew.themoviedatabase.data.model.PersonDetails?>) {
                       if (p1.isSuccessful) {
                           callback(p1)
                       }
                   }

                   override fun onFailure(p0: Call<com.drew.themoviedatabase.data.model.PersonDetails?>, p1: Throwable) {
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
                castApiService.getPersonPhotos(personId, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale()).enqueue(object : Callback<PersonPhotosResponse?> {
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

    suspend fun getCastPhotos(personId: Int) : List<com.drew.themoviedatabase.data.model.Photos?>? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response =   castApiService.getPersonPhotos(
                            personId = personId,
                            apiKey = com.drew.themoviedatabase.data.remote.API_KEY,
                            language = defaultLocale()
                        )?.execute()
                        if (response?.isSuccessful == true) {
                            response.body()?.getPersonPhotos()
                        } else {
                            emptyList()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        emptyList()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getPopularPeople(page : Int) : List<com.drew.themoviedatabase.data.model.PopularPerson?>? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response =   castApiService.getPopularPeople(
                            apiKey = com.drew.themoviedatabase.data.remote.API_KEY,
                            language = defaultLocale(),
                            page = page
                        )?.execute()
                        if (response?.isSuccessful == true) {
                            response.body()?.results
                        } else {
                            emptyList()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        emptyList()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getCombinedCredits(personId: Int, callback: (Response<CombinedCreditsResponse?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                castApiService.getCombinedCredits(personId,
                    com.drew.themoviedatabase.data.remote.API_KEY, defaultLocale()).enqueue(object : Callback<CombinedCreditsResponse?> {
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