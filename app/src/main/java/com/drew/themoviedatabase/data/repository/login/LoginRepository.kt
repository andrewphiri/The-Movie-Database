package com.drew.themoviedatabase.data.repository.login

import android.util.Log
import com.drew.themoviedatabase.data.model.UserProfile
import com.drew.themoviedatabase.data.remote.CreateSessionResponse
import com.drew.themoviedatabase.data.remote.LoginApiService
import com.drew.themoviedatabase.data.remote.RequestTokenResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val loginService: LoginApiService
) {
    suspend fun requestToken() : RequestTokenResponse? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response = loginService.createRequestToken(apiKey = com.drew.themoviedatabase.data.remote.API_KEY)?.execute()
                        Log.d("LOGIN_REPOSITORY", "Response: ${response?.body()}")
                        if (response?.isSuccessful == true) {
                            response.body()
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun createSessionID(requestToken: String) : CreateSessionResponse? {
        val jsonBody = JsonObject().apply {
            addProperty("request_token", requestToken)
        }
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response = loginService.createSessionID(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, jsonBody = jsonBody )?.execute()

                        if (response?.isSuccessful == true) {
                            response.body()
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getAccountID(sessionId: String) : UserProfile? {

        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response = loginService.getAccountId(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, sessionId = sessionId )?.execute()

                        if (response?.isSuccessful == true) {
                            response.body()
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteSessionID(sessionId: String) : Response<com.drew.themoviedatabase.data.model.DeleteSession>? {
        val jsonBody = JsonObject().apply {
            addProperty("session_id", sessionId)
        }
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response = loginService.deleteSession(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, jsonBody = jsonBody )?.execute()

                        if (response?.isSuccessful == true) {
                            response
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}