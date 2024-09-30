package com.drew.themoviedatabase.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.drew.themoviedatabase.Network.API_KEY
import com.drew.themoviedatabase.Network.CastApiService
import com.drew.themoviedatabase.Network.CombinedCreditsResponse
import com.drew.themoviedatabase.Network.PersonPhotosResponse
import com.drew.themoviedatabase.POJO.PersonDetails
import com.drew.themoviedatabase.POJO.Photos
import com.drew.themoviedatabase.Utilities.defaultLocale
import com.drew.themoviedatabase.Utilities.imageLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class PersonRepository @Inject constructor(
    private val castApiService: CastApiService
) {

    fun getCastImagesPager(personId: Int) : Flow<PagingData<Photos>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { CastPhotosPagingSource(this, personId) }
        ).flow
    }

    fun getPersonDetails(personId : Int, callback: (Response<PersonDetails?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
               castApiService.getPersonDetails(personId, apiKey = API_KEY, language = defaultLocale()).enqueue(object :
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
                castApiService.getPersonPhotos(personId, apiKey = API_KEY, language = defaultLocale()).enqueue(object : Callback<PersonPhotosResponse?> {
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

    suspend fun getCastPhotos(personId: Int) : List<Photos?>? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response =   castApiService.getPersonPhotos(
                            personId = personId,
                            apiKey = API_KEY,
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

    fun getCombinedCredits(personId: Int, callback: (Response<CombinedCreditsResponse?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                castApiService.getCombinedCredits(personId, API_KEY, defaultLocale()).enqueue(object : Callback<CombinedCreditsResponse?> {
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