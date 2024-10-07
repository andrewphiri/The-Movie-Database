package com.drew.themoviedatabase.Network

import android.net.Uri
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.themoviedb.org/3/"
const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
const val API_KEY = "Provide own key"
const val IMAGE_SIZE = "w300" // You can use different sizes like w200, w300, original, etc.
const val TRAILER_BASE_URL = "https://www.youtube.com/watch?v="


class NetworkClient {
    var retrofit: Retrofit? = null
    fun getRetrofitClient() : Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }
        return retrofit
    }

    fun getPosterUrl(posterPath: String?, imageSize: String = IMAGE_SIZE): String {
        return if (!posterPath.isNullOrEmpty()) {
            "$IMAGE_BASE_URL$imageSize$posterPath"
        } else {
            "No image available"
        }
    }
    fun getTrailerUrl(trailerPath: String?): String {
        return if (!trailerPath.isNullOrEmpty()) {
            Uri.parse("$TRAILER_BASE_URL$trailerPath")
            "$TRAILER_BASE_URL$trailerPath"
        } else {
            "No trailer available"
        }
    }
    fun tokenRequestAndAppRedirectUrl(request_Token: String?): String {
        val myAppURL = "mdbapp://www.drew.mdb.com?request_token=$request_Token"
        return "https://www.themoviedb.org/authenticate/$request_Token?redirect_to=${myAppURL}"
    }
}