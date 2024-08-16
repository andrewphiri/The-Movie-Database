package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.PersonDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CastApiService {
    @GET("person/{person_id}")
    fun getPersonDetails(
        @Path("person_id",) personId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("append_to_response") appendToResponse: String = "combined_credits,images"): Call<PersonDetails?>

    @GET("person/{person_id}/combined_credits")
    fun getCombinedCredits(
        @Path("person_id",) personId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String): Call<CombinedCreditsResponse?>

    @GET("person/{person_id}/images")
    fun getPersonPhotos(
        @Path("person_id",) personId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String): Call<PersonPhotosResponse?>

}