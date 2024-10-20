package com.drew.themoviedatabase.data.remote

import android.net.Uri
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.themoviedb.org/3/"
const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
const val API_KEY = "Provide your own API key"
const val IMAGE_SIZE = "w300" // You can use different sizes like w200, w300, original, etc.
const val TRAILER_BASE_URL = "https://www.youtube.com/watch?v="
const val YOUTUBE_BASE_URL = "https://youtube.googleapis.com/youtube/v3/"
const val YOUTUBE_API_KEY = "Provide your own API key"


class NetworkClient {
    var retrofit: Retrofit? = null
    fun getRetrofitClient() : Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(com.drew.themoviedatabase.data.remote.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }
        return retrofit
    }

    fun getPosterUrl(posterPath: String?, imageSize: String = com.drew.themoviedatabase.data.remote.IMAGE_SIZE): String {
        return if (!posterPath.isNullOrEmpty()) {
            "${com.drew.themoviedatabase.data.remote.IMAGE_BASE_URL}$imageSize$posterPath"
        } else {
            "No image available"
        }
    }
    fun getTrailerUrl(trailerPath: String?): String {
        return if (!trailerPath.isNullOrEmpty()) {
            Uri.parse("${com.drew.themoviedatabase.data.remote.TRAILER_BASE_URL}$trailerPath")
            "${com.drew.themoviedatabase.data.remote.TRAILER_BASE_URL}$trailerPath"
        } else {
            "No trailer available"
        }
    }
    /**
     * Builds the authentication redirect URL for the Movie Database API.
     * @param requestToken The request token obtained from the API.
     * @return The complete redirect URL.
     */
    fun buildAuthenticationRedirectUrl(requestToken: String?): String {
        val myAppURL = "mdbapp://www.drew.mdb.com?request_token=$requestToken"
        return "https://www.themoviedb.org/authenticate/$requestToken?redirect_to=${myAppURL}"
    }
}