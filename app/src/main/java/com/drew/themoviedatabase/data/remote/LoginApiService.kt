package com.drew.themoviedatabase.data.remote

import com.drew.themoviedatabase.data.model.UserProfile
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginApiService {

    @GET("authentication/token/new")
    fun createRequestToken(
        @Query("api_key") apiKey: String,
    ): Call<RequestTokenResponse?>?

    @POST("authentication/session/new")
    fun createSessionID(
        @Body jsonBody: JsonObject,
        @Query("api_key") apiKey: String
    ) : Call<CreateSessionResponse>?

    @GET("account")
    fun getAccountId(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ) : Call<UserProfile?>?

    @POST("authentication/session/new")
    fun deleteSession(
        @Body jsonBody: JsonObject,
        @Query("api_key") apiKey: String
    ) : Call<com.drew.themoviedatabase.data.model.DeleteSession>?
}